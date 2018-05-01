package com.mabrcosta.keysmanager.users.data

import java.util.UUID

import com.byteslounge.slickrepo.meta.Entity

case class UsersAccessProvider(id: Option[UUID] = Some(UUID.randomUUID())) extends Entity[UsersAccessProvider, UUID] {

  override def withId(id: UUID): UsersAccessProvider = copy(id = Some(id))
}
