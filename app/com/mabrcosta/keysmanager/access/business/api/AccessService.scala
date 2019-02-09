package com.mabrcosta.keysmanager.access.business.api

import com.mabrcosta.keysmanager.access.data.{AccessProvider, AccessProviderCreationData, AccessProviderData}
import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.machines.business.api.{_machinesErrorEither, _machinesGroupsErrorEither}
import com.mabrcosta.keysmanager.users.business.api.{_usersErrorEither, _usersGroupsErrorEither}
import com.mabrcosta.keysmanager.users.data.Key
import org.atnos.eff.Eff

trait AccessService[TIOIn[_], TIOOut[_]] extends BaseService[TIOIn, TIOOut] {

  def getAuthorizedKeys[R: _TIOOut: _machinesErrorEither](hostname: String): Eff[R, Seq[Key]]

  def add[R: _TIOOut: _machinesErrorEither: _machinesGroupsErrorEither: _usersErrorEither: _usersGroupsErrorEither](
      accessProvider: AccessProviderCreationData): Eff[R, AccessProviderData]

  def delete[R: _TIOOut: _accessErrorEither](accessProviderId: EntityId[AccessProvider]): Eff[R, Boolean]

}
