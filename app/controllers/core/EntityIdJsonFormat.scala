package controllers.core

import java.util.UUID

import com.mabrcosta.keysmanager.core.data.EntityId
import play.api.libs.json._

object EntityIdJsonFormat {
  import com.mabrcosta.keysmanager.core.data.EntityIdOps._

  implicit def userEntityIdWrites[T] = new Writes[EntityId[T]] {
    def writes(userId: EntityId[T]) = JsString(userId.value.toString)
  }

  implicit def userEntityIdReads[T] = new Reads[EntityId[T]] {
    override def reads(json: JsValue): JsResult[EntityId[T]] = JsSuccess(UUID.fromString(json.as[String]).toEntityId[T])
  }
}
