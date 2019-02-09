package com.mabrcosta.keysmanager.machines.business.api

import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.machines.data.{Machine, MachinesGroup}
import org.atnos.eff.Eff

trait MachinesGroupsService[TDBIO[_], TDBOut[_]] extends BaseService[TDBIO, TDBOut] {

  def get[R: _tDBOut: _machinesGroupsErrorEither](machinesGroupId: EntityId[MachinesGroup]): Eff[R, MachinesGroup]

  def getWithMachine[R: _tDBOut](machineId: EntityId[Machine]): Eff[R, Seq[MachinesGroup]]

}
