package com.mabrcosta.keysmanager.machines.data

import java.util.UUID

import com.byteslounge.slickrepo.meta.Entity

case class MachinesAccessProvider(id: Option[UUID] = Some(UUID.randomUUID()))
    extends Entity[MachinesAccessProvider, UUID] {

  override def withId(id: UUID): MachinesAccessProvider = copy(id = Some(id))
}
