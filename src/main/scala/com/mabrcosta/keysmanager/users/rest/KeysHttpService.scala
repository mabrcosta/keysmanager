package com.mabrcosta.keysmanager.users.rest

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.mabrcosta.keysmanager.core.persistence.util.DBIOEffect._
import com.mabrcosta.keysmanager.core.persistence.util.JdbcProfileAsyncDatabase
import com.mabrcosta.keysmanager.users.business.api
import com.mabrcosta.keysmanager.users.business.api.{NotFound => _, _}
import com.mabrcosta.keysmanager.users.data.{Key, KeyData}
import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import org.atnos.eff._
import org.atnos.eff.concurrent.Scheduler
import org.atnos.eff.syntax.all._
import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class KeysHttpService @Inject()(private val keyService: KeysService[DBIO, TimedFuture],
                                private val keyServiceDBIO: KeysService[DBIO, DBIO],
                                implicit val executionContext: ExecutionContext,
                                implicit val scheduler: Scheduler,
                                private val database: JdbcProfileAsyncDatabase)
    extends KeysJsonSupport
    with LazyLogging {

  val routes: Route = pathPrefix("users" / JavaUUID / "keys") { uidOwner =>
    get {
      handleResponseDBIO[Seq[Key]](keyServiceDBIO.getForOwner[KeysDBIOStack], uidOwner, keys => complete(keys))
    } ~ post {
      entity(as[KeyData]) { key =>
        handleResponseDBIO[Key](keyServiceDBIO.addKey[KeysDBIOStack](key.value), uidOwner, key => complete(key))
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
    val result = database.withTransaction { implicit sessionDatabase =>
      FutureInterpretation.runAsync(effect.runDBIO.runReader(uidOwner).runEither)
    }
    responseHandler[T](result, response)
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
