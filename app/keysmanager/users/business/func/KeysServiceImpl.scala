package com.mabrcosta.keysmanager.users.business.func

import java.util.UUID

import com.mabrcosta.keysmanager.core.business.api.{Error, NotFound}
import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.users.business.api.{KeysService, _errorEither, _ownerReader}
import com.mabrcosta.keysmanager.users.data.Key
import com.mabrcosta.keysmanager.users.persistence.api.KeysDal
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect._
import org.atnos.eff.ReaderEffect._

import scala.concurrent.ExecutionContext

class KeysServiceImpl[TDBIO[_], TDBOut[_]] @Inject()(
      private val keysDal: KeysDal[TDBIO],
      private val effectsDatabaseExecutor: EffectsDatabaseExecutor[TDBIO, TDBOut],
      implicit val executionContext: ExecutionContext)
    extends KeysService[TDBIO, TDBOut] {

  import effectsDatabaseExecutor._

  override def getForOwner[R: _tDBOut: _ownerReader]: Eff[R, Seq[Key]] = {
    for {
      uidOwner <- ask
      keys <- keysDal.findForOwner(uidOwner).execute
    } yield keys
  }

  override def getForOwners[R: _tDBOut](uidOwners: Seq[UUID]): Eff[R, Seq[Key]] = {
    keysDal.findForOwners(uidOwners).execute
  }

  override def add[R: _tDBOut: _ownerReader](keyValue: String): Eff[R, Key] = {
    for {
      uidOwner <- ask
      key = Key(value = keyValue, uidOwnerSubject = uidOwner)
      res <- keysDal.save(key).execute
    } yield res
  }

  override def delete[R: _tDBOut: _ownerReader: _errorEither](uid: UUID): Eff[R, Boolean] = {
    for {
      uidOwner <- ask
      keyOpt <- keysDal.findForOwner(uid, uidOwner).execute
      key <- if (keyOpt.isDefined) right(keyOpt.get)
      else left[R, Error, Key](NotFound(s"Unable to find key for uid $uid"))
      _ <- keysDal.delete(key).execute
    } yield true
  }

}
