package com.mabrcosta.keysmanager.users.business.func

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.users.business.api.{UserNotFound, UsersError, UsersService, _usersErrorEither}
import com.mabrcosta.keysmanager.users.data.User
import com.mabrcosta.keysmanager.users.persistence.api.UsersDal
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect.{left, right}

import scala.concurrent.ExecutionContext

class UsersServiceImpl[TDBIO[_], TDBOut[_]] @Inject()(
    private[this] val usersDal: UsersDal[TDBIO],
    private[this] val effectsDatabaseExecutor: EffectsDatabaseExecutor[TDBIO, TDBOut],
    implicit val executionContext: ExecutionContext)
    extends UsersService[TDBIO, TDBOut] {

  import effectsDatabaseExecutor._

  override def get[R: _tDBOut: _usersErrorEither](uidUser: UUID): Eff[R, User] = {
    for {
      userOpt <- usersDal.find(uidUser).execute
      user <- if (userOpt.isDefined) right(userOpt.get)
      else left[R, UsersError, User](UserNotFound(s"Unable to find user for uid $uidUser"))
    } yield user
  }

  override def get[R: _tDBOut : _usersErrorEither](uidUsers: Seq[UUID]): Eff[R, Seq[User]] = {
    usersDal.find(uidUsers).execute
  }

  override def getWithProviders[R: _tDBOut](uidUsersProviders: Seq[UUID]): Eff[R, Seq[User]] = {
    usersDal.findForUserAccessProviders(uidUsersProviders).execute
  }
}
