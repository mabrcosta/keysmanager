package com.mabrcosta.keysmanager.access.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.{BaseRepositoryEntity, EntityId}
import com.mabrcosta.keysmanager.machines.data.MachineAccessProvider
import com.mabrcosta.keysmanager.users.data.UserAccessProvider

case class AccessProvider(id: Option[EntityId[AccessProvider]] = Some(EntityId()),
                          userAccessProviderId: EntityId[UserAccessProvider],
                          machineAccessProviderId: EntityId[MachineAccessProvider],
                          startInstant: Instant,
                          endInstant: Instant,
                          uidCreatorUser: Option[UUID] = None,
                          uidLastModifierUser: Option[UUID] = None,
                          creationInstant: Instant = Instant.now(),
                          updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[AccessProvider, EntityId[AccessProvider]] {

  override def withId(id: EntityId[AccessProvider]): AccessProvider = copy(id = Some(id))
}
