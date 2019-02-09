package com.mabrcosta.keysmanager.users.business.api

import com.mabrcosta.keysmanager.core.business.api.BaseService
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.users.data.{User, UserAccessProvider}
import org.atnos.eff.Eff

trait UsersService[TIOIn[_], TIOOut[_]] extends BaseService[TIOIn, TIOOut] {

  def get[R: _TIOOut: _usersErrorEither](userId: EntityId[User]): Eff[R, User]

  def get[R: _TIOOut: _usersErrorEither](userIds: Seq[EntityId[User]]): Eff[R, Seq[User]]

  def getWithProviders[R: _TIOOut](userAccessProviderIds: Seq[EntityId[UserAccessProvider]]): Eff[R, Seq[User]]

}
