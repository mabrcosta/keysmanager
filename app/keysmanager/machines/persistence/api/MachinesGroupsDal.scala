package com.mabrcosta.keysmanager.machines.persistence.api

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.DatabaseDal
import com.mabrcosta.keysmanager.machines.data.MachinesGroup

trait MachinesGroupsDal[TIO[_]] extends DatabaseDal[MachinesGroup, UUID, TIO] {

}
