package com.mabrcosta.keysmanager.machines.data

import com.byteslounge.slickrepo.meta.Entity
import com.mabrcosta.keysmanager.core.data.EntityId

case class MachineAccessProvider(id: Option[EntityId[MachineAccessProvider]] = Some(EntityId()))
    extends Entity[MachineAccessProvider, EntityId[MachineAccessProvider]] {

  override def withId(id: EntityId[MachineAccessProvider]): MachineAccessProvider = copy(id = Some(id))
}
