package com.mabrcosta.keysmanager.access.rest

import com.mabrcosta.keysmanager.access.data._
import com.mabrcosta.keysmanager.core.rest.CoreSprayJsonSupport
import com.mabrcosta.keysmanager.machines.data.{Machine, MachinesGroup}
import com.mabrcosta.keysmanager.machines.rest.MachinesJsonSupport
import com.mabrcosta.keysmanager.users.data.{User, UsersGroup}
import com.mabrcosta.keysmanager.users.rest.UsersJsonSupport
import spray.json.{JsObject, JsString, JsValue, RootJsonFormat, deserializationError}

trait AccessJsonSupport extends CoreSprayJsonSupport with MachinesJsonSupport with UsersJsonSupport {
  implicit val userAccessCreationDataFormat: RootJsonFormat[UserAccessCreationData] = jsonFormat1(
    UserAccessCreationData)
  implicit val usersGroupAccessCreationDataFormat: RootJsonFormat[UsersGroupAccessCreationData] = jsonFormat1(
    UsersGroupAccessCreationData)

  implicit val machineAccessCreationDataFormat: RootJsonFormat[MachineAccessCreationData] = jsonFormat1(
    MachineAccessCreationData)
  implicit val machinesGroupAccessCreationDataFormat: RootJsonFormat[MachinesGroupAccessCreationData] = jsonFormat1(
    MachinesGroupAccessCreationData)

  private val KEY_TYPE = "_type"
  private val KEY_SOURCE = "_source"

  private def typeSourceWriter[T](typeValue: String, obj: JsValue): JsObject = {
    JsObject(
      KEY_TYPE -> JsString(typeValue),
      KEY_SOURCE -> obj
    )
  }

  implicit object UserAccessCreationDataJsonFormat
      extends RootJsonFormat[Either[UsersGroupAccessCreationData, UserAccessCreationData]] {

    private val VALUE_TYPE_USER = "user"
    private val VALUE_TYPE_USERS_GROUP = "users_group"

    def write(data: Either[UsersGroupAccessCreationData, UserAccessCreationData]): JsObject = data match {
      case Right(user) => typeSourceWriter(VALUE_TYPE_USER, userAccessCreationDataFormat.write(user))
      case Left(group) => typeSourceWriter(VALUE_TYPE_USERS_GROUP, usersGroupAccessCreationDataFormat.write(group))
    }

    def read(value: JsValue): Either[UsersGroupAccessCreationData, UserAccessCreationData] = {
      value.asJsObject.getFields(KEY_TYPE, KEY_SOURCE) match {
        case Seq(JsString(VALUE_TYPE_USER), source)        => Right(source.convertTo[UserAccessCreationData])
        case Seq(JsString(VALUE_TYPE_USERS_GROUP), source) => Left(source.convertTo[UsersGroupAccessCreationData])
        case other ⇒
          deserializationError(
            "Cannot deserialize User Access for UserAccessCreationData: invalid input. Raw input: " + other)
      }
    }
  }

  implicit object MachineAccessCreationDataJsonFormat
      extends RootJsonFormat[Either[MachinesGroupAccessCreationData, MachineAccessCreationData]] {

    private val VALUE_TYPE_MACHINE = "machine"
    private val VALUE_TYPE_MACHINES_GROUP = "machines_group"

    def write(data: Either[MachinesGroupAccessCreationData, MachineAccessCreationData]): JsObject = data match {
      case Right(machine) => typeSourceWriter(VALUE_TYPE_MACHINE, machineAccessCreationDataFormat.write(machine))
      case Left(group) =>typeSourceWriter(VALUE_TYPE_MACHINES_GROUP, machinesGroupAccessCreationDataFormat.write(group))
    }

    def read(value: JsValue): Either[MachinesGroupAccessCreationData, MachineAccessCreationData] = {
      value.asJsObject.getFields(KEY_TYPE, KEY_SOURCE) match {
        case Seq(JsString(VALUE_TYPE_MACHINE), source)        => Right(source.convertTo[MachineAccessCreationData])
        case Seq(JsString(VALUE_TYPE_MACHINES_GROUP), source) => Left(source.convertTo[MachinesGroupAccessCreationData])
        case other ⇒
          deserializationError(
            "Cannot deserialize Machine Access for MachineAccessCreationData: invalid input. Raw input: " + other)
      }
    }
  }

  implicit val accessProviderCreationDataFormat: RootJsonFormat[AccessProviderCreationData] = jsonFormat4(
    AccessProviderCreationData)

  implicit object UserAccessDataJsonFormat extends RootJsonFormat[Either[UsersGroup, User]] {

    private val VALUE_TYPE_USER = "user"
    private val VALUE_TYPE_USERS_GROUP = "users_group"

    def write(data: Either[UsersGroup, User]): JsObject = data match {
      case Right(user) => typeSourceWriter(VALUE_TYPE_USER, userFormat.write(user))
      case Left(group) => typeSourceWriter(VALUE_TYPE_USERS_GROUP, usersGroupFormat.write(group))
    }

    def read(value: JsValue): Either[UsersGroup, User] = {
      value.asJsObject.getFields(KEY_TYPE, KEY_SOURCE) match {
        case Seq(JsString(VALUE_TYPE_USER), source)        => Right(source.convertTo[User])
        case Seq(JsString(VALUE_TYPE_USERS_GROUP), source) => Left(source.convertTo[UsersGroup])
        case other ⇒
          deserializationError(
            "Cannot deserialize User Access for UserAccessData: invalid input. Raw input: " + other)
      }
    }
  }

  implicit object MachineAccessDataJsonFormat extends RootJsonFormat[Either[MachinesGroup, Machine]] {

    private val VALUE_TYPE_MACHINE = "machine"
    private val VALUE_TYPE_MACHINES_GROUP = "machines_group"

    def write(data: Either[MachinesGroup, Machine]): JsObject = data match {
      case Right(machine) => typeSourceWriter(VALUE_TYPE_MACHINE, machineFormat.write(machine))
      case Left(group) =>typeSourceWriter(VALUE_TYPE_MACHINES_GROUP, machinesGroupFormat.write(group))
    }

    def read(value: JsValue): Either[MachinesGroup, Machine] = {
      value.asJsObject.getFields(KEY_TYPE, KEY_SOURCE) match {
        case Seq(JsString(VALUE_TYPE_MACHINE), source)        => Right(source.convertTo[Machine])
        case Seq(JsString(VALUE_TYPE_MACHINES_GROUP), source) => Left(source.convertTo[MachinesGroup])
        case other ⇒
          deserializationError(
            "Cannot deserialize Machine Access for MachineAccessData: invalid input. Raw input: " + other)
      }
    }
  }

  implicit val accessProviderDataFormat: RootJsonFormat[AccessProviderData] = jsonFormat5(AccessProviderData.apply)

}
