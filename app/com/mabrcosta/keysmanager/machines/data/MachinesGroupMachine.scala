package com.mabrcosta.keysmanager.machines.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.{BaseRepositoryEntity, EntityId}

case class MachinesGroupMachine(id: Option[EntityId[MachinesGroupMachine]] = Some(EntityId()),
                                machineId: EntityId[Machine],
                                machinesGroupId: EntityId[MachinesGroup],
                                uidCreatorUser: Option[UUID] = None,
                                uidLastModifierUser: Option[UUID] = None,
                                creationInstant: Instant = Instant.now(),
                                updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[MachinesGroupMachine, EntityId[MachinesGroupMachine]] {

  override def withId(id: EntityId[MachinesGroupMachine]): MachinesGroupMachine = copy(id = Some(id))
}
