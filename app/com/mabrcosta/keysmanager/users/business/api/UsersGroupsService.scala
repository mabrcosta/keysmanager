package com.mabrcosta.keysmanager.users.business.api

import java.util.UUID

import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.users.data.{User, UsersGroup, UsersGroupUser}
import org.atnos.eff.Eff

trait UsersGroupsService[TDBIO[_], TDBOut[_]] extends BaseService[TDBIO, TDBOut] {

  def get[R: _tDBOut: _usersGroupsErrorEither](uidUsersGroup: UUID): Eff[R, UsersGroup]

  def getWithProviders[R : _tDBOut](uidUsersProviders: Seq[UUID]): Eff[R, Seq[UsersGroup]]

  def getUsers[R : _tDBOut](uidUsersGroups: Seq[UUID]): Eff[R, Seq[UsersGroupUser]]

}
