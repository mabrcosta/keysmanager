package com.mabrcosta.keysmanager.users.persistence.api

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.DatabaseDal
import com.mabrcosta.keysmanager.users.data.{UserAccessProvider, UsersGroup}

trait UsersGroupsDal[TIO[_]] extends DatabaseDal[UsersGroup, EntityId[UsersGroup], TIO] {

  def findForUserAccessProviders(usersAccessProviderIds: Seq[EntityId[UserAccessProvider]]): TIO[Seq[UsersGroup]]

}
