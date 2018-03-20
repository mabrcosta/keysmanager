package com.mabrcosta.keysmanager.core.persistence

import java.time.Instant
import java.util.UUID

import com.byteslounge.slickrepo.meta.Entity


trait BaseRepositoryEntity[TEntity <: Entity[TEntity, TUID], TUID] extends SimpleRepositoryEntity[TEntity, TUID] {
  val uidCreatorSubject: Option[UUID]
  val uidLastModifierSubject: Option[UUID]
  val creationTimestamp: Instant
  val updateTimestamp: Instant
}

