package com.mabrcosta.keysmanager.machines.business.api

import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.machines.data.Machine
import org.atnos.eff.Eff

trait MachinesService[TIOIn[_], TIOOut[_]] extends BaseService[TIOIn, TIOOut] {

  def get[R : _TIOOut: _machinesErrorEither](machineId: EntityId[Machine]): Eff[R, Machine]

  def getForHostname[R : _TIOOut: _machinesErrorEither](hostname: String): Eff[R, Machine]


}
