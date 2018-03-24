package com.mabrcosta.keysmanager.users.key.rest

import com.mabrcosta.keysmanager.core.rest.CoreSprayJsonSupport
import com.mabrcosta.keysmanager.users.key.data.Key
import spray.json.RootJsonFormat

trait KeyJsonSupport extends CoreSprayJsonSupport {
  implicit val keyFormat: RootJsonFormat[Key] = jsonFormat7(Key)

//  implicit object KeyJsonFormat extends RootJsonFormat[Key] {
//    def write(key: Key): JsObject = {
//      val fields = Map.newBuilder[String, JsValue] +=
//        ("uid" -> JsString(key.uid.toString)) +=
//        ("value" -> JsString(key.value)) +=
//        ("uid_owner_subject" -> JsString(key.uidOwnerSubject.toString)) +=
//        ("creation_timestamp" -> JsString(key.creationTimestamp.toString)) +=
//        ("update_timestamp" -> JsString(key.updateTimestamp.toString))
//
//      if (key.uidCreatorSubject.isDefined) fields += ("uid_creator_subject" -> JsString(key.uidCreatorSubject.get.toString))
//      if (key.uidLastModifierSubject.isDefined) fields += ("uid_lastModifier_subject" -> JsString(key.uidLastModifierSubject.get.toString))
//
//      JsObject(fields.result)
//    }
//    def read(value: JsValue): Key = {
//      val jsObject = value.asJsObject
//      jsObject.getFields("value", "uid_owner_subject") match {
//        case Seq(JsString(name), JsNumber(red), JsNumber(green), JsNumber(blue)) =>
//          new Color(name, red.toInt, green.toInt, blue.toInt)
//        case _ => throw new DeserializationException("Color expected")
//      }
//    }
//  }
}