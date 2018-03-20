package com.mabrcosta.keysmanager.core.persistence

import com.byteslounge.slickrepo.meta.Entity


trait SimpleRepositoryEntity[TEntity <: Entity[TEntity, TUID], TUID] extends Entity[TEntity, TUID] {
  val uid: TUID
  val id = Some(uid)
}
