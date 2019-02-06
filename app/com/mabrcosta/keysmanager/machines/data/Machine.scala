package com.mabrcosta.keysmanager.machines.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.BaseRepositoryEntity

case class Machine(id: Option[UUID] = Some(UUID.randomUUID()),
                   name: String,
                   hostname: String,
                   uidMachineAccessProvider: UUID,
                   uidCreatorUser: Option[UUID] = None,
                   uidLastModifierUser: Option[UUID] = None,
                   creationInstant: Instant = Instant.now(),
                   updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[Machine, UUID] {

  override def withId(id: UUID): Machine = copy(id = Some(id))
}
