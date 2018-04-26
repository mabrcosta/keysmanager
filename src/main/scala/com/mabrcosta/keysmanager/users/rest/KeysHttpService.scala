package com.mabrcosta.keysmanager.users.rest

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.mabrcosta.keysmanager.users.business.api.{NotFound => _, _}
import com.mabrcosta.keysmanager.users.business.{KeysStackInterpreter, api}
import com.mabrcosta.keysmanager.users.data.{Key, KeyData}
import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import org.atnos.eff._
import slick.dbio.DBIO

import scala.concurrent.Future
import scala.util.{Failure, Success}

class KeysHttpService @Inject()(private val keyService: KeysService[DBIO, DBIO],
                                private val keysStackInterpreter: KeysStackInterpreter)
    extends KeysJsonSupport
    with LazyLogging {

  val routes: Route = pathPrefix("users" / JavaUUID / "keys") { uidOwner =>
    get {
      handleResponseDBIO[Seq[Key]](keyService.getForOwner[KeysDBIOStack], uidOwner, keys => complete(keys))
    } ~ post {
      entity(as[KeyData]) { key =>
        handleResponseDBIO[Key](keyService.addKey[KeysDBIOStack](key.value), uidOwner, key => complete(key))
      }
    } ~ path(JavaUUID) { uidKey =>
      delete {
        handleResponseDBIO[Boolean](keyService.deleteKey[KeysDBIOStack](uidKey),
                                    uidOwner,
                                    res => if (res) complete("") else complete(InternalServerError))
      }
    }
  }

  def handleResponseDBIO[T](effect: Eff[KeysDBIOStack, T], uidOwner: UUID, response: T => Route): Route = {
    responseHandler[T](keysStackInterpreter.run(effect, uidOwner), response)
  }

  def responseHandler[T](result: Future[Either[Error, T]], response: T => Route): Route =
    onComplete(result) {
      case Success(Right(res))  => response(res)
      case Success(Left(error)) => errorMapping(error)
      case Failure(ex) => {
        logger.error(ex.getMessage, ex)
        complete(InternalServerError, ex.getMessage)
      }
    }

  def errorMapping(error: api.Error): Route = error match {
    case api.NotFound(message) => complete(NotFound, message)
  }

}
