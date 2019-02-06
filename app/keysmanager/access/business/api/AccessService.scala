package com.mabrcosta.keysmanager.access.business.api

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.access.data.{AccessProviderCreationData, AccessProviderData}
import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.users.business.api._errorEither
import com.mabrcosta.keysmanager.users.data.Key
import org.atnos.eff.Eff

trait AccessService[TDBIO[_], TDBOut[_]] extends BaseService[TDBIO, TDBOut] {

  def getAuthorizedKeys[R: _tDBOut: _errorEither](uidMachineProviders: Seq[UUID], at: Instant): Eff[R, Seq[Key]]

  def add[R: _tDBOut: _errorEither](accessProvider: AccessProviderCreationData): Eff[R, AccessProviderData]

  def delete[R: _tDBOut: _errorEither](uidProvider: UUID): Eff[R, Boolean]

}
