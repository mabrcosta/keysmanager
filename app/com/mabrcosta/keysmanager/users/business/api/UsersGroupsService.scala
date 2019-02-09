package com.mabrcosta.keysmanager.users.business.api

import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.users.data.{UserAccessProvider, UsersGroup, UsersGroupUser}
import org.atnos.eff.Eff

trait UsersGroupsService[TIOIn[_], TIOOut[_]] extends BaseService[TIOIn, TIOOut] {

  def get[R: _TIOOut: _usersGroupsErrorEither](usersGroupId: EntityId[UsersGroup]): Eff[R, UsersGroup]

  def getWithProviders[R : _TIOOut](userAccessProviderIds: Seq[EntityId[UserAccessProvider]]): Eff[R, Seq[UsersGroup]]

  def getUsers[R : _TIOOut](usersGroupIds: Seq[EntityId[UsersGroup]]): Eff[R, Seq[UsersGroupUser]]

}
