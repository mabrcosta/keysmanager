package com.mabrcosta.keysmanager.users.key.persistence.api

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.DatabaseDal
import com.mabrcosta.keysmanager.users.key.data.Key

trait KeysDal[TIO[_]] extends DatabaseDal[Key, UUID, TIO] {
  def getForOwner(uidOwner: UUID): TIO[Seq[Key]]
}
