package com.mabrcosta.keysmanager.users.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.BaseRepositoryEntity

case class UsersGroupUser(id: Option[UUID] = Some(UUID.randomUUID()),
                          uidUser: UUID,
                          uidUsersGroup: UUID,
                          uidCreatorUser: Option[UUID] = None,
                          uidLastModifierUser: Option[UUID] = None,
                          creationInstant: Instant = Instant.now(),
                          updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[UsersGroupUser, UUID] {

  override def withId(id: UUID): UsersGroupUser = copy(id = Some(id))
}
