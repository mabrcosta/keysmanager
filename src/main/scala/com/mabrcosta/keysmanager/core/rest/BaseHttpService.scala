package com.mabrcosta.keysmanager.core.rest

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes.{InternalServerError, NotFound}
import akka.http.scaladsl.server.Directives.{complete, onComplete}
import akka.http.scaladsl.server.Route
import com.mabrcosta.keysmanager.core.business
import com.mabrcosta.keysmanager.core.business.StackInterpreter
import com.mabrcosta.keysmanager.users.business.api.Stack
import com.typesafe.scalalogging.LazyLogging
import com.mabrcosta.keysmanager.core.business.api.{NotFound => NotFoundError}
import org.atnos.eff.Eff

import scala.concurrent.Future
import scala.util.{Failure, Success}

class BaseHttpService(private val stackInterpreter: StackInterpreter) extends LazyLogging {

  def handleResponse[T](effect: Eff[Stack, T], uidOwner: UUID, response: T => Route): Route = {
    responseHandler[T](stackInterpreter.run(effect, uidOwner), response)
  }

  def responseHandler[T](result: Future[Either[business.api.Error, T]], response: T => Route): Route =
    onComplete(result) {
      case Success(Right(res))  => response(res)
      case Success(Left(error)) => errorMapping(error)
      case Failure(ex) => {
        logger.error(ex.getMessage, ex)
        complete(InternalServerError, ex.getMessage)
      }
    }

  def errorMapping(error: business.api.Error): Route = error match {
    case NotFoundError(message) => complete(NotFound, message)
  }

}
