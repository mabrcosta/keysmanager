package com.mabrcosta.keysmanager.users.business.func

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.users.business.api._
import com.mabrcosta.keysmanager.users.data.{Key, User}
import com.mabrcosta.keysmanager.users.persistence.api.KeysDal
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect._
import org.atnos.eff.ReaderEffect._

import scala.concurrent.ExecutionContext

class KeysServiceImpl[TDBIO[_], TDBOut[_]] @Inject()(
    private[this] val keysDal: KeysDal[TDBIO],
    private[this] val effectsDatabaseExecutor: EffectsDatabaseExecutor[TDBIO, TDBOut],
    implicit val executionContext: ExecutionContext)
    extends KeysService[TDBIO, TDBOut] {

  import effectsDatabaseExecutor._

  override def getForOwner[R: _tDBOut: _ownerReader]: Eff[R, Seq[Key]] = {
    for {
      uidOwner <- ask
      keys <- keysDal.findForOwner(uidOwner).execute
    } yield keys
  }

  override def getForOwners[R: _tDBOut](ownerUserIds: Seq[EntityId[User]]): Eff[R, Seq[Key]] = {
    keysDal.findForOwners(ownerUserIds).execute
  }

  override def add[R: _tDBOut: _ownerReader](keyValue: String): Eff[R, Key] = {
    for {
      uidOwner <- ask
      key = Key(value = keyValue, ownerUserId = uidOwner)
      res <- keysDal.save(key).execute
    } yield res
  }

  override def delete[R: _tDBOut: _ownerReader: _keysErrorEither](keyId: EntityId[Key]): Eff[R, Boolean] = {
    for {
      ownerUserId <- ask
      keyOpt <- keysDal.findForOwner(keyId, ownerUserId).execute
      key <- if (keyOpt.isDefined) right(keyOpt.get)
      else left[R, KeysError, Key](KeyNotFound(s"Unable to find key for id $keyId"))
      _ <- keysDal.delete(key).execute
    } yield true
  }

}
