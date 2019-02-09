package com.mabrcosta.keysmanager.users.business.api

import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.users.data.{UserAccessProvider, UsersGroup, UsersGroupUser}
import org.atnos.eff.Eff

trait UsersGroupsService[TDBIO[_], TDBOut[_]] extends BaseService[TDBIO, TDBOut] {

  def get[R: _tDBOut: _usersGroupsErrorEither](usersGroupId: EntityId[UsersGroup]): Eff[R, UsersGroup]

  def getWithProviders[R : _tDBOut](userAccessProviderIds: Seq[EntityId[UserAccessProvider]]): Eff[R, Seq[UsersGroup]]

  def getUsers[R : _tDBOut](usersGroupIds: Seq[EntityId[UsersGroup]]): Eff[R, Seq[UsersGroupUser]]

}
