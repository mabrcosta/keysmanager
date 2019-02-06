package com.mabrcosta.keysmanager.machines.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.BaseRepositoryEntity

case class MachinesGroupMachine(id: Option[UUID] = Some(UUID.randomUUID()),
                                uidMachine: UUID,
                                uidMachinesGroup: UUID,
                                uidCreatorUser: Option[UUID] = None,
                                uidLastModifierUser: Option[UUID] = None,
                                creationInstant: Instant = Instant.now(),
                                updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[MachinesGroupMachine, UUID] {

  override def withId(id: UUID): MachinesGroupMachine = copy(id = Some(id))
}
