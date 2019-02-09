package com.mabrcosta.keysmanager.machines.business.api

import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.machines.data.{Machine, MachinesGroup}
import org.atnos.eff.Eff

trait MachinesGroupsService[TIOIn[_], TIOOut[_]] extends BaseService[TIOIn, TIOOut] {

  def get[R: _TIOOut: _machinesGroupsErrorEither](machinesGroupId: EntityId[MachinesGroup]): Eff[R, MachinesGroup]

  def getWithMachine[R: _TIOOut](machineId: EntityId[Machine]): Eff[R, Seq[MachinesGroup]]

}
