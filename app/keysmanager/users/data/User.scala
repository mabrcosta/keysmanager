package com.mabrcosta.keysmanager.users.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.BaseRepositoryEntity

case class User(id: Option[UUID] = Some(UUID.randomUUID()),
                firstName: String,
                lastName: String,
                uidUserAccessProvider: UUID,
                uidCreatorUser: Option[UUID] = None,
                uidLastModifierUser: Option[UUID] = None,
                creationInstant: Instant = Instant.now(),
                updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[User, UUID] {

  override def withId(id: UUID): User = copy(id = Some(id))
}
