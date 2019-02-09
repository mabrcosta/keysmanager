package com.mabrcosta.keysmanager.users.business.api

import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.users.data.{Key, User}
import org.atnos.eff.Eff

trait KeysService[TIOIn[_], TIOOut[_]] extends BaseService[TIOIn, TIOOut] {

  def getForOwner[R : _TIOOut: _ownerReader]: Eff[R, Seq[Key]]

  def getForOwners[R : _TIOOut](ownerUserIds: Seq[EntityId[User]]): Eff[R, Seq[Key]]

  def add[R : _TIOOut : _ownerReader](keyValue: String): Eff[R, Key]

  def delete[R : _TIOOut : _ownerReader : _keysErrorEither](keyId: EntityId[Key]): Eff[R, Boolean]

}
