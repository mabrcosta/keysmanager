package com.mabrcosta.keysmanager.users.key.rest

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.mabrcosta.keysmanager.users.key.business.api.{KeyService, KeyStack}
import com.mabrcosta.keysmanager.users.key.data.KeyData
import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import org.atnos.eff.FutureInterpretation._
import org.atnos.eff.concurrent.Scheduler
import org.atnos.eff.syntax.all._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class KeysHttpService @Inject()(private val keyService: KeyService, implicit val scheduler: Scheduler)
    extends KeyJsonSupport
    with LazyLogging {

  val routes: Route = pathPrefix("users" / JavaUUID / "keys") { uidOwner =>
    get {
      onComplete(runAsync(keyService.getForOwner[KeyStack].runReader(uidOwner).runEither)) {
        case Success(Right(keys)) => complete(keys)
        case Success(Left(_))     => complete(NotFound)
        case Failure(ex)          => complete(InternalServerError, ex.getMessage)
      }
    } ~ post {
      entity(as[KeyData]) { key =>
        onComplete(runAsync(keyService.addKey[KeyStack](key.value).runReader(uidOwner).runEither)) {
          case Success(Right(res)) => complete(res)
          case Success(Left(_))     => complete(NotFound)
          case Failure(ex)          => complete(InternalServerError, ex.getMessage)
        }
      }
    } ~ path(JavaUUID) { uidKey =>
      delete {
        onComplete(runAsync(keyService.deleteKey[KeyStack](uidKey).runReader(uidOwner).runEither)) {
          case Success(Right(true)) => complete("")
          case Success(Right(false)) => complete(InternalServerError)
          case Success(Left(_)) => complete(NotFound)
          case Failure(ex) => complete(InternalServerError, ex.getMessage)
        }
      }
    }
  }

}
