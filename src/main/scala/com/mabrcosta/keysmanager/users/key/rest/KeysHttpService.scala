package com.mabrcosta.keysmanager.users.key.rest

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import com.mabrcosta.keysmanager.users.key.business.api.{KeyService, KeyStack}
import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import org.atnos.eff.FutureInterpretation._
import org.atnos.eff._
import org.atnos.eff.concurrent.Scheduler
import org.atnos.eff.syntax.all._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class KeysHttpService @Inject()(private val keyService: KeyService) extends KeyJsonSupport with LazyLogging {

  implicit val scheduler: Scheduler = ExecutorServices.schedulerFromGlobalExecutionContext

  implicit def myExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case ex: Throwable => {
        logger.error(ex.getMessage, ex)
        complete(InternalServerError)
      }
    }

  val routes: Route =
    handleExceptions(myExceptionHandler) {
      path("users" / JavaUUID / "keys") { uidOwner =>
        get {
          onComplete(runAsync(keyService.getForOwner[KeyStack](uidOwner).runEither)) {
            case Success(Right(keys)) => complete(keys)
            case Success(Left(_))     => complete(NotFound)
            case Failure(ex)          => complete(InternalServerError, ex.getMessage)
          }
        }
      }
    }

}
