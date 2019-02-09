package com.mabrcosta.keysmanager.users.data

import com.byteslounge.slickrepo.meta.Entity
import com.mabrcosta.keysmanager.core.data.EntityId

case class UserAccessProvider(id: Option[EntityId[UserAccessProvider]] = Some(EntityId()))
    extends Entity[UserAccessProvider, EntityId[UserAccessProvider]] {

  override def withId(id: EntityId[UserAccessProvider]): UserAccessProvider = copy(id = Some(id))
}
