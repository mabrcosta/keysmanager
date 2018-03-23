package com.mabrcosta.keysmanager.users.key.rest

import java.time.Instant
import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import com.mabrcosta.keysmanager.users.key.business.{KeyService, KeyStack}
import com.mabrcosta.keysmanager.users.key.persistence.Key
import org.atnos.eff._
import org.atnos.eff.FutureInterpretation._
import org.atnos.eff.concurrent.Scheduler
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat}
import syntax.all._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

trait KeyJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(uuid: UUID) = JsString(uuid.toString)
    def read(value: JsValue) = {
      value match {
        case JsString(uuid) => UUID.fromString(uuid)
        case _              => throw DeserializationException("Expected hexadecimal UUID string")
      }
    }
  }

  implicit object InstantFormat extends JsonFormat[Instant] {
    import java.time.format.DateTimeFormatter
    val formatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

    def write(instant: Instant) = JsString(instant.toString)
    def read(value: JsValue) = {
      value match {
        case JsString(instant) => Instant.from(formatter.parse(instant))
        case _              => throw DeserializationException("Expected hexadecimal UUID string")
      }
    }
  }

  implicit val keyFormat: RootJsonFormat[Key] = jsonFormat7(Key)
}

class KeysHttpService(private val keyService: KeyService) extends KeyJsonSupport {

  implicit val scheduler: Scheduler = ExecutorServices.schedulerFromGlobalExecutionContext

  def getUserKeys = path("user" / JavaUUID / "keys") { uidOwner =>
    get {
        onComplete(runAsync(keyService.getForOwner[KeyStack](uidOwner).runEither)) {
          case Success(Left(keys)) => complete(keys)
          case _ => reject
        }
    }
  }

}
