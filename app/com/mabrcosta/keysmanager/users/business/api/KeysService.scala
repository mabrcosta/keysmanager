package com.mabrcosta.keysmanager.users.business.api

import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.users.data.{Key, User}
import org.atnos.eff.Eff

trait KeysService[TDBIO[_], TDBOut[_]] extends BaseService[TDBIO, TDBOut] {

  def getForOwner[R : _tDBOut: _ownerReader]: Eff[R, Seq[Key]]

  def getForOwners[R : _tDBOut](ownerUserIds: Seq[EntityId[User]]): Eff[R, Seq[Key]]

  def add[R : _tDBOut : _ownerReader](keyValue: String): Eff[R, Key]

  def delete[R : _tDBOut : _ownerReader : _keysErrorEither](keyId: EntityId[Key]): Eff[R, Boolean]

}
