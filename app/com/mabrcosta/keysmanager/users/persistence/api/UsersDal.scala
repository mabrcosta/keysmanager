package com.mabrcosta.keysmanager.users.persistence.api

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.DatabaseDal
import com.mabrcosta.keysmanager.users.data.{User, UsersGroup}

trait UsersDal[TIO[_]] extends DatabaseDal[User, UUID, TIO] {

  def findForUserAccessProviders(uidUsersAccessProviders: Seq[UUID]): TIO[Seq[User]]

}
