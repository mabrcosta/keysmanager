package com.mabrcosta.keysmanager.users.key.business

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.DatabaseExecutor
import com.mabrcosta.keysmanager.core.persistence.EffectsDatabaseExecutor._
import com.mabrcosta.keysmanager.users.key.persistence.{Key, KeysDal}
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect._
import org.atnos.eff.future._
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global

trait KeyService {
  def getForOwner[R: _future: _throwableEither](uidOwner: UUID): Eff[R, Seq[Key]]
}

class KeyServiceImpl(private val keysDal: KeysDal[DBIO],
                               private val databaseExecutor: DatabaseExecutor[DBIO, _future],
                               implicit val db: JdbcProfile#Backend#Database,
                               implicit val profile: JdbcProfile)
    extends KeyService {

  lazy val throwException = new Exception()

  override def getForOwner[R: _future: _throwableEither](uidOwner: UUID): Eff[R, Seq[Key]] = {
    for {
      keys <- keysDal.getForOwner(uidOwner).execute
      res <- if (keys.isEmpty) right(keys) else left[R, Throwable, Seq[Key]](throwException)
    } yield res
  }

  def add[R: _future : _throwableEither](uidOwner: UUID, keyValue: String): Eff[R, Key] = {
    val key = Key(value = keyValue, ownerSubjectId = uidOwner)
    keysDal.save(key).execute
  }

  def delete[R: _future : _throwableEither](uid: UUID): Eff[R, Boolean] = {
    for {
      keyOpt <- keysDal.find(uid).execute
      key <- if (keyOpt.isDefined) right(keyOpt.get) else left[R, Throwable, Key](throwException)
      _ <- keysDal.delete(key).execute
    } yield true
  }

}
