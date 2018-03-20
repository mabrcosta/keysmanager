package com.mabrcosta.keysmanager.machines.key.persistence

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.BaseRepositoryEntity

case class Key(uid: UUID,
               value: String,
               ownerSubjectId: UUID,
               uidCreatorSubject: Option[UUID] = None,
               uidLastModifierSubject: Option[UUID] = None,
               creationTimestamp: Instant,
               updateTimestamp: Instant)
    extends BaseRepositoryEntity[Key, UUID] {

  override def withId(id: UUID): Key = copy(uid = id)
}
