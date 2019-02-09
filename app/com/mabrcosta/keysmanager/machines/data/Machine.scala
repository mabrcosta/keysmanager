package com.mabrcosta.keysmanager.machines.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.{BaseRepositoryEntity, EntityId}

case class Machine(id: Option[EntityId[Machine]] = Some(EntityId()),
                   name: String,
                   hostname: String,
                   machineAccessProviderId: EntityId[MachineAccessProvider],
                   uidCreatorUser: Option[UUID] = None,
                   uidLastModifierUser: Option[UUID] = None,
                   creationInstant: Instant = Instant.now(),
                   updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[Machine, EntityId[Machine]] {

  override def withId(id: EntityId[Machine]): Machine = copy(id = Some(id))
}
