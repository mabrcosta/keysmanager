package com.mabrcosta.keysmanager.users.key.persistence

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.BaseRepositoryEntity

case class Key(uid: UUID = UUID.randomUUID(),
               value: String,
               ownerSubjectId: UUID,
               uidCreatorSubject: Option[UUID] = None,
               uidLastModifierSubject: Option[UUID] = None,
               creationTimestamp: Instant = Instant.now(),
               updateTimestamp: Instant = Instant.now())
    extends BaseRepositoryEntity[Key, UUID] {

  override def withId(id: UUID): Key = copy(uid = id)
}
