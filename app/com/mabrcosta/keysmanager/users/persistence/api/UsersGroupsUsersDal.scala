package com.mabrcosta.keysmanager.users.persistence.api

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.DatabaseDal
import com.mabrcosta.keysmanager.users.data.{UsersGroup, UsersGroupUser}

trait UsersGroupsUsersDal[TIO[_]] extends DatabaseDal[UsersGroupUser, EntityId[UsersGroupUser], TIO] {

  def findForUserGroups(usersGroupIds: Seq[EntityId[UsersGroup]]): TIO[Seq[UsersGroupUser]]

}
