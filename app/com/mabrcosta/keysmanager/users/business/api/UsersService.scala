package com.mabrcosta.keysmanager.users.business.api

import java.util.UUID

import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.users.data.User
import org.atnos.eff.Eff

trait UsersService[TDBIO[_], TDBOut[_]] extends BaseService[TDBIO, TDBOut] {

  def get[R : _tDBOut: _usersErrorEither](uidUser: UUID): Eff[R, User]

  def get[R : _tDBOut: _usersErrorEither](uidUsers: Seq[UUID]): Eff[R, Seq[User]]

  def getWithProviders[R : _tDBOut](uidUsersProviders: Seq[UUID]): Eff[R, Seq[User]]

}
