package com.mabrcosta.keysmanager.users.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.BaseRepositoryEntity

case class UsersGroup(id: Option[UUID] = Some(UUID.randomUUID()),
                      name: String,
                      uidUserAccessProvider: UUID,
                      uidCreatorUser: Option[UUID] = None,
                      uidLastModifierUser: Option[UUID] = None,
                      creationInstant: Instant = Instant.now(),
                      updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[UsersGroup, UUID] {

  override def withId(id: UUID): UsersGroup = copy(id = Some(id))
}
