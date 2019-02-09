package com.mabrcosta.keysmanager.machines.persistence.api

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.DatabaseDal
import com.mabrcosta.keysmanager.machines.data.MachinesGroup

trait MachinesGroupsDal[TIO[_]] extends DatabaseDal[MachinesGroup, EntityId[MachinesGroup], TIO] {

}
