package com.mabrcosta.keysmanager.users.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.{BaseRepositoryEntity, EntityId}

case class UsersGroup(id: Option[EntityId[UsersGroup]] = Some(EntityId()),
                      name: String,
                      userAccessProviderId: EntityId[UserAccessProvider],
                      uidCreatorUser: Option[UUID] = None,
                      uidLastModifierUser: Option[UUID] = None,
                      creationInstant: Instant = Instant.now(),
                      updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[UsersGroup, EntityId[UsersGroup]] {

  override def withId(id: EntityId[UsersGroup]): UsersGroup = copy(id = Some(id))
}
