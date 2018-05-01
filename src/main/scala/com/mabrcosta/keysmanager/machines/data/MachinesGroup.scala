package com.mabrcosta.keysmanager.machines.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.BaseRepositoryEntity

case class MachinesGroup(id: Option[UUID] = Some(UUID.randomUUID()),
                         name: String,
                         uidMachineAccessProvider: UUID,
                         uidCreatorUser: Option[UUID] = None,
                         uidLastModifierUser: Option[UUID] = None,
                         creationInstant: Instant = Instant.now(),
                         updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[MachinesGroup, UUID] {

  override def withId(id: UUID): MachinesGroup = copy(id = Some(id))
}
