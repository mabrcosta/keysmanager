package com.mabrcosta.keysmanager.machines.key.business

import java.util.UUID

import cats.~>
import com.mabrcosta.keysmanager.machines.key.persistence.{Key, KeysDal}

trait KeyService[M[_]] {
  def getForOwner(uidOwner: UUID): M[List[Key]]
}

class KeyFEService[M[_], MDBIO[_]](private val keysDal: KeysDal[MDBIO], private val evalDb: MDBIO ~> M)
    extends KeyService[M] {

  def getForOwner(uidOwner: UUID): M[List[Key]] = ???

}
