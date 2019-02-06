package com.mabrcosta.keysmanager.users.persistence.api

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.DatabaseDal
import com.mabrcosta.keysmanager.users.data.UsersGroupUser

trait UsersGroupsUsersDal[TIO[_]] extends DatabaseDal[UsersGroupUser, UUID, TIO] {

  def findForUserGroups(uidUsersGroups: Seq[UUID]): TIO[Seq[UsersGroupUser]]

}
