package com.mabrcosta.keysmanager.machines.persistence.api

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.DatabaseDal
import com.mabrcosta.keysmanager.machines.data.Machine

trait MachinesDal[TIO[_]] extends DatabaseDal[Machine, UUID, TIO] {

  def findForHostname(hostname: String): TIO[Option[Machine]]

}
