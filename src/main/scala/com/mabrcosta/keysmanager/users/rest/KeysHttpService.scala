package com.mabrcosta.keysmanager.users.rest

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.mabrcosta.keysmanager.core.persistence.util.DBIOEffect._
import com.mabrcosta.keysmanager.core.persistence.util.FutureDatabaseExecutor
import com.mabrcosta.keysmanager.users.business.api
import com.mabrcosta.keysmanager.users.business.api.{NotFound => _, _}
import com.mabrcosta.keysmanager.users.data.{Key, KeyData}
import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import org.atnos.eff.FutureInterpretation._
import org.atnos.eff._
import org.atnos.eff.concurrent.Scheduler
import org.atnos.eff.syntax.all._
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class KeysHttpService @Inject()(private val keyService: KeysService[DBIO, TimedFuture],
                                private val keyServiceDBIO: KeysService[DBIO, DBIO],
                                implicit val executionContext: ExecutionContext,
                                implicit val scheduler: Scheduler,
                                implicit val futureDatabaseExecutor: FutureDatabaseExecutor)
    extends KeysJsonSupport
    with LazyLogging {

  val routes: Route = pathPrefix("users" / JavaUUID / "keys") { uidOwner =>
    get {
//      handleResponse[Seq[Key]](keyService.getForOwner[KeysStack], uidOwner, keys => complete(keys))
      handleResponseDBIO[Seq[Key]](keyServiceDBIO.getForOwner[KeysDBIOStack], uidOwner, keys => complete(keys))
    } ~ post {
      entity(as[KeyData]) { key =>
//        handleResponse[Key](keyService.addKey[KeysStack](key.value), uidOwner, key => complete(key))
        val res = for {
          key1 <- keyServiceDBIO.addKey[KeysDBIOStack](key.value)
          key2 <- keyServiceDBIO.addKey[KeysDBIOStack](key.value)
        } yield {
          key2
        }

        val temp = runAsync(res.runDBIO.runReader(uidOwner).runEither)
//        val key1 = keyServiceDBIO.addKey[KeysDBIOStack](key.value)
//        val key2 = keyServiceDBIO.addKey[KeysDBIOStack](key.value)
//
//        val key1DBIO: DBIO[Key] = Eff.detach(key1)

        handleResponseDBIO[Key](res, uidOwner, key => complete(key))
      }
    } ~ path(JavaUUID) { uidKey =>
      delete {
        handleResponse[Boolean](keyService.deleteKey[KeysStack](uidKey),
                                uidOwner,
                                res => if (res) complete("") else complete(InternalServerError))

//        handleResponseDBIO[Boolean](keyServiceDBIO.deleteKey[KeysDBIOStack](uidKey),
//          uidOwner,
//          res => if (res) complete("") else complete(InternalServerError))
      }
    }
  }

  def handleResponse[T](effect: Eff[KeysStack, T], uidOwner: UUID, response: T => Route): Route = {
    onComplete(runAsync(effect.runReader(uidOwner).runEither)) {
      case Success(Right(res))  => response(res)
      case Success(Left(error)) => errorMapping(error)
      case Failure(ex)          => {
        logger.error(ex.getMessage, ex)
        complete(InternalServerError, ex.getMessage)
      }
    }
  }

  def handleResponseDBIO[T](effect: Eff[KeysDBIOStack, T], uidOwner: UUID, response: T => Route): Route = {
//    val temp: Eff[KeysStack, T] = runDBIO[KeysDBIOStack, KeysStack, T](effect)
    handleResponse[T](effect.runDBIO, uidOwner, response)
  }

  def errorMapping(error: api.Error): Route = error match {
    case api.NotFound(message) => complete(NotFound, message)
  }

}
