package com.mabrcosta.keysmanager.machines.persistence.api

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.DatabaseDal
import com.mabrcosta.keysmanager.machines.data.MachinesGroupMachine

trait MachinesGroupMachinesDal[TIO[_]] extends DatabaseDal[MachinesGroupMachine, UUID, TIO] {

  def findForMachine(uidMachine: UUID): TIO[Seq[MachinesGroupMachine]]

}
