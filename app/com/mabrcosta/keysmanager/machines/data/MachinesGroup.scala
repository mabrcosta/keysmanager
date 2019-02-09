package com.mabrcosta.keysmanager.machines.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.{BaseRepositoryEntity, EntityId}

case class MachinesGroup(id: Option[EntityId[MachinesGroup]] = Some(EntityId()),
                         name: String,
                         machineAccessProviderId: EntityId[MachineAccessProvider],
                         uidCreatorUser: Option[UUID] = None,
                         uidLastModifierUser: Option[UUID] = None,
                         creationInstant: Instant = Instant.now(),
                         updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[MachinesGroup, EntityId[MachinesGroup]] {

  override def withId(id: EntityId[MachinesGroup]): MachinesGroup = copy(id = Some(id))
}
