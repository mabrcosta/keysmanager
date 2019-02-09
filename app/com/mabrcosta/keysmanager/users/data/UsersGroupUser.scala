package com.mabrcosta.keysmanager.users.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.{BaseRepositoryEntity, EntityId}

case class UsersGroupUser(id: Option[EntityId[UsersGroupUser]] = Some(EntityId()),
                          userId: EntityId[User],
                          usersGroupId: EntityId[UsersGroup],
                          uidCreatorUser: Option[UUID] = None,
                          uidLastModifierUser: Option[UUID] = None,
                          creationInstant: Instant = Instant.now(),
                          updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[UsersGroupUser, EntityId[UsersGroupUser]] {

  override def withId(id: EntityId[UsersGroupUser]): UsersGroupUser = copy(id = Some(id))
}
