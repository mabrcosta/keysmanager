package com.mabrcosta.keysmanager.machines.business.api

import java.util.UUID

import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.machines.data.MachinesGroup
import org.atnos.eff.Eff

trait MachinesGroupsService[TDBIO[_], TDBOut[_]] extends BaseService[TDBIO, TDBOut] {

  def getWithMachine[R : _tDBOut](uidMachine: UUID): Eff[R, Seq[MachinesGroup]]

  def get[R: _tDBOut: _machinesGroupsErrorEither](uidMachinesGroup: UUID): Eff[R, MachinesGroup]

}
