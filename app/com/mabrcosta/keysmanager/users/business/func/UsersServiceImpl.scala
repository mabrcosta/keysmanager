package com.mabrcosta.keysmanager.users.business.func

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.users.business.api.{UserNotFound, UsersError, UsersService, _usersErrorEither}
import com.mabrcosta.keysmanager.users.data.{User, UserAccessProvider}
import com.mabrcosta.keysmanager.users.persistence.api.UsersDal
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect.{left, right}

import scala.concurrent.ExecutionContext

class UsersServiceImpl[TIOIn[_], TIOOut[_]] @Inject()(
    private[this] val usersDal: UsersDal[TIOIn],
    private[this] val effectsDatabaseExecutor: EffectsDatabaseExecutor[TIOIn, TIOOut],
    implicit val executionContext: ExecutionContext)
    extends UsersService[TIOIn, TIOOut] {

  import effectsDatabaseExecutor._

  override def get[R: _TIOOut: _usersErrorEither](userId: EntityId[User]): Eff[R, User] = {
    for {
      userOpt <- usersDal.find(userId).execute
      user <- if (userOpt.isDefined) right(userOpt.get)
      else left[R, UsersError, User](UserNotFound(s"Unable to find user for id $userId"))
    } yield user
  }

  override def get[R: _TIOOut: _usersErrorEither](userIds: Seq[EntityId[User]]): Eff[R, Seq[User]] = {
    usersDal.find(userIds).execute
  }

  override def getWithProviders[R: _TIOOut](
      userAccessProviderIds: Seq[EntityId[UserAccessProvider]]): Eff[R, Seq[User]] = {
    usersDal.findForUserAccessProviders(userAccessProviderIds).execute
  }

}
