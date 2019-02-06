package com.mabrcosta.keysmanager.access.business.api

import java.util.UUID

import com.mabrcosta.keysmanager.access.data.{AccessProviderCreationData, AccessProviderData}
import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.machines.business.api.{_machinesErrorEither, _machinesGroupsErrorEither}
import com.mabrcosta.keysmanager.users.business.api.{_usersErrorEither, _usersGroupsErrorEither}
import com.mabrcosta.keysmanager.users.data.Key
import org.atnos.eff.Eff

trait AccessService[TDBIO[_], TDBOut[_]] extends BaseService[TDBIO, TDBOut] {

  def getAuthorizedKeys[R: _tDBOut: _machinesErrorEither](hostname: String): Eff[R, Seq[Key]]

  def add[R: _tDBOut: _machinesErrorEither: _machinesGroupsErrorEither: _usersErrorEither: _usersGroupsErrorEither](
      accessProvider: AccessProviderCreationData): Eff[R, AccessProviderData]

  def delete[R: _tDBOut: _accessErrorEither](uidProvider: UUID): Eff[R, Boolean]

}
