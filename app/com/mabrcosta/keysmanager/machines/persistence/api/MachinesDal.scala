package com.mabrcosta.keysmanager.machines.persistence.api

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.DatabaseDal
import com.mabrcosta.keysmanager.machines.data.Machine

trait MachinesDal[TIO[_]] extends DatabaseDal[Machine, EntityId[Machine], TIO] {

  def findForHostname(hostname: String): TIO[Option[Machine]]

}
