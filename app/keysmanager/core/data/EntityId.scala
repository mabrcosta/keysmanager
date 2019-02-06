package com.mabrcosta.keysmanager.core.data

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.api.SingleId

case class EntityId[T](value: UUID = UUID.randomUUID()) extends SingleId[T, UUID]

object EntityIdOps {
  implicit class EntityIdUUIDCreator(uid: UUID) {
    def toEntityId[T]: EntityId[T] = EntityId[T](uid)
  }
}
