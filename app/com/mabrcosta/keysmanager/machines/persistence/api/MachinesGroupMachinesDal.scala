package com.mabrcosta.keysmanager.machines.persistence.api

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.DatabaseDal
import com.mabrcosta.keysmanager.machines.data.{Machine, MachinesGroupMachine}

trait MachinesGroupMachinesDal[TIO[_]] extends DatabaseDal[MachinesGroupMachine, EntityId[MachinesGroupMachine], TIO] {

  def findForMachine(machineId: EntityId[Machine]): TIO[Seq[MachinesGroupMachine]]

}
