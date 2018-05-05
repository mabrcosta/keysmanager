package com.mabrcosta.keysmanager.machines.business.api

import java.util.UUID

import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.machines.data.Machine
import com.mabrcosta.keysmanager.users.business.api._errorEither
import com.mabrcosta.keysmanager.users.data.Key
import org.atnos.eff.Eff

trait MachinesService[TDBIO[_], TDBOut[_]] extends BaseService[TDBIO, TDBOut] {

  def getAuthorizedKeys[R : _tDBOut: _errorEither](hostname: String): Eff[R, Seq[Key]]

  def get[R : _tDBOut: _errorEither](uidMachine: UUID): Eff[R, Machine]


}
