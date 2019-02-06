package com.mabrcosta.keysmanager.access.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.BaseRepositoryEntity

case class AccessProvider(id: Option[UUID] = Some(UUID.randomUUID()),
                          uidUserAccessProvider: UUID,
                          uidMachineAccessProvider: UUID,
                          startInstant: Instant,
                          endInstant: Instant,
                          uidCreatorUser: Option[UUID] = None,
                          uidLastModifierUser: Option[UUID] = None,
                          creationInstant: Instant = Instant.now(),
                          updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[AccessProvider, UUID] {

  override def withId(id: UUID): AccessProvider = copy(id = Some(id))
}
