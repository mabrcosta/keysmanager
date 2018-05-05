package com.mabrcosta.keysmanager.access.business.api

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.access.data.{AccessProvider, AccessProviderData}
import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.users.business.api._errorEither
import org.atnos.eff.Eff

trait AccessService[TDBIO[_], TDBOut[_]] extends BaseService[TDBIO, TDBOut] {

  def getForMachines[R: _tDBOut](uidMachineProviders: Seq[UUID], at: Instant): Eff[R, Seq[AccessProvider]]

  def add[R: _tDBOut: _errorEither](accessProvider: AccessProviderData): Eff[R, AccessProvider]

  def delete[R: _tDBOut: _errorEither](uidProvider: UUID): Eff[R, Boolean]

}
