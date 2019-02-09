package com.mabrcosta.keysmanager.users.persistence.api

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.DatabaseDal
import com.mabrcosta.keysmanager.users.data.{Key, User}

trait KeysDal[TIO[_]] extends DatabaseDal[Key, EntityId[Key], TIO] {

  def findForOwner(userId: EntityId[User]): TIO[Seq[Key]]

  def findForOwners(userIds: Seq[EntityId[User]]): TIO[Seq[Key]]

  def findForOwner(keyId: EntityId[Key], userId: EntityId[User]): TIO[Option[Key]]

}
