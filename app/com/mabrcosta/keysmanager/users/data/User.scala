package com.mabrcosta.keysmanager.users.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.{BaseRepositoryEntity, EntityId}

case class User(id: Option[EntityId[User]] = Some(EntityId()),
                firstName: String,
                lastName: String,
                userAccessProviderId: EntityId[UserAccessProvider],
                uidCreatorUser: Option[UUID] = None,
                uidLastModifierUser: Option[UUID] = None,
                creationInstant: Instant = Instant.now(),
                updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[User, EntityId[User]] {

  override def withId(id: EntityId[User]): User = copy(id = Some(id))
}
