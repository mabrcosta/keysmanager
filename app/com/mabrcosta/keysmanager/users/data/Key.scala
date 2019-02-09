package com.mabrcosta.keysmanager.users.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.{BaseRepositoryEntity, EntityId}

case class Key(id: Option[EntityId[Key]] = Some(EntityId()),
               value: String,
               ownerUserId: EntityId[User],
               uidCreatorUser: Option[UUID] = None,
               uidLastModifierUser: Option[UUID] = None,
               creationInstant: Instant = Instant.now(),
               updateInstant: Instant = Instant.now())
    extends BaseRepositoryEntity[Key, EntityId[Key]] {

  override def withId(id: EntityId[Key]): Key = copy(id = Some(id))
}
