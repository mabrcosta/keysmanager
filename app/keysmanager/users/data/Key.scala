package com.mabrcosta.keysmanager.users.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.BaseRepositoryEntity

case class Key(id: Option[UUID] = Some(UUID.randomUUID()),
               value: String,
               uidOwnerSubject: UUID,
               uidCreatorUser: Option[UUID] = None,
               uidLastModifierUser: Option[UUID] = None,
               creationInstant: Instant = Instant.now(),
               updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[Key, UUID] {

  override def withId(id: UUID): Key = copy(id = Some(id))
}
