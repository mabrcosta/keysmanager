package com.mabrcosta.keysmanager.access.persistence.api

import java.time.Instant

import com.mabrcosta.keysmanager.access.data.AccessProvider
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.DatabaseDal
import com.mabrcosta.keysmanager.machines.data.MachineAccessProvider

trait AccessProvidersDal[TIO[_]] extends DatabaseDal[AccessProvider, EntityId[AccessProvider], TIO] {

  def findForMachinesProviders(machinesProviderIds: Seq[EntityId[MachineAccessProvider]],
                               at: Instant): TIO[Seq[AccessProvider]]

}
