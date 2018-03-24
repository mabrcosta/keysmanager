package com.mabrcosta.keysmanager.users.key.rest

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.mabrcosta.keysmanager.users.key.business.api
import com.mabrcosta.keysmanager.users.key.business.api.{KeysStack, KeysService}
import com.mabrcosta.keysmanager.users.key.data.KeyData
import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import org.atnos.eff.FutureInterpretation._
import org.atnos.eff.TimedFuture
import org.atnos.eff.concurrent.Scheduler
import org.atnos.eff.syntax.all._
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class KeysHttpService @Inject()(private val keyService: KeysService[DBIO, TimedFuture],
                                implicit val scheduler: Scheduler)
    extends KeyJsonSupport
    with LazyLogging {

  val routes: Route = pathPrefix("users" / JavaUUID / "keys") { uidOwner =>
    get {
      onComplete(runAsync(keyService.getForOwner[KeysStack].runReader(uidOwner).runEither)) {
        case Success(Right(keys)) => complete(keys)
        case Success(Left(error)) => errorMapping(error)
        case Failure(ex)          => complete(InternalServerError, ex.getMessage)
      }
    } ~ post {
      entity(as[KeyData]) { key =>
        onComplete(runAsync(keyService.addKey[KeysStack](key.value).runReader(uidOwner).runEither)) {
          case Success(Right(res))  => complete(res)
          case Success(Left(error)) => errorMapping(error)
          case Failure(ex)          => complete(InternalServerError, ex.getMessage)
        }
      }
    } ~ path(JavaUUID) { uidKey =>
      delete {
        onComplete(runAsync(keyService.deleteKey[KeysStack](uidKey).runReader(uidOwner).runEither)) {
          case Success(Right(true))  => complete("")
          case Success(Right(false)) => complete(InternalServerError)
          case Success(Left(error))  => errorMapping(error)
          case Failure(ex)           => complete(InternalServerError, ex.getMessage)
        }
      }
    }
  }

  def errorMapping(error: api.Error): Route = error match {
    case api.NotFound(message) => complete(NotFound, message)
  }

}
