package com.mabrcosta.keysmanager.users.business.api

import java.util.UUID

import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.users.data.User
import org.atnos.eff.Eff

trait UsersService[TDBIO[_], TDBOut[_]] extends BaseService[TDBIO, TDBOut] {

  def get[R : _tDBOut: _errorEither](uidUser: UUID): Eff[R, User]

}
