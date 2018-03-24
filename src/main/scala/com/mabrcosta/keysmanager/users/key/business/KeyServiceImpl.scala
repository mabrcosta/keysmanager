package com.mabrcosta.keysmanager.users.key.business

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor._
import com.mabrcosta.keysmanager.users.key.business.api.{KeyService, NotFound, _errorEither}
import com.mabrcosta.keysmanager.users.key.data.Key
import com.mabrcosta.keysmanager.users.key.persistence.KeysDal
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect._
import org.atnos.eff.future._
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global

class KeyServiceImpl @Inject()(private val keysDal: KeysDal[DBIO],
                               implicit val db: JdbcProfile#Backend#Database,
                               implicit val profile: JdbcProfile)
    extends KeyService {

  override def getForOwner[R: _future: _errorEither](uidOwner: UUID): Eff[R, Seq[Key]] = {
    for {
      keys <- keysDal.getForOwner(uidOwner).execute
    } yield keys
  }

  override def addKey[R: _future: _errorEither](uidOwner: UUID, keyValue: String): Eff[R, Key] = {
    val key = Key(value = keyValue, uidOwnerSubject = uidOwner)
    keysDal.save(key).execute
  }

  override def deleteKey[R: _future: _errorEither](uid: UUID): Eff[R, Boolean] = {
    for {
      keyOpt <- keysDal.find(uid).execute
      key <- if (keyOpt.isDefined) right(keyOpt.get) else left[R, api.Error, Key](NotFound(s"Unable to find key for uid $uid"))
      _ <- keysDal.delete(key).execute
    } yield true
  }

}
