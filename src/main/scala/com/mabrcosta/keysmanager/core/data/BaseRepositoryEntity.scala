package com.mabrcosta.keysmanager.core.data

import java.time.Instant
import java.util.UUID

import com.byteslounge.slickrepo.meta.Entity


trait BaseRepositoryEntity[TEntity <: Entity[TEntity, TUID], TUID] extends Entity[TEntity, TUID] {
  val uidCreatorUser: Option[UUID]
  val uidLastModifierUser: Option[UUID]
  val creationInstant: Instant
  val updateInstant: Instant
}

