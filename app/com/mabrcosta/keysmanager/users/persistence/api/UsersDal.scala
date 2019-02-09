package com.mabrcosta.keysmanager.users.persistence.api

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.DatabaseDal
import com.mabrcosta.keysmanager.users.data.{User, UserAccessProvider}

trait UsersDal[TIO[_]] extends DatabaseDal[User, EntityId[User], TIO] {

  def findForUserAccessProviders(usersAccessProviderIds: Seq[EntityId[UserAccessProvider]]): TIO[Seq[User]]

}
