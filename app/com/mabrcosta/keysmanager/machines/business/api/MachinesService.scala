package com.mabrcosta.keysmanager.machines.business.api

import java.util.UUID

import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.machines.data.Machine
import org.atnos.eff.Eff

trait MachinesService[TDBIO[_], TDBOut[_]] extends BaseService[TDBIO, TDBOut] {

  def get[R : _tDBOut: _machinesErrorEither](uidMachine: UUID): Eff[R, Machine]

  def getForHostname[R : _tDBOut: _machinesErrorEither](hostname: String): Eff[R, Machine]


}
