package com.mabrcosta.keysmanager.users.business

import java.util.UUID

import com.mabrcosta.keysmanager.core.business.api.{Error, NotFound}
import com.mabrcosta.keysmanager.core.persistence.DatabaseDal
import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.users.business.api.{UsersGroupsService, _errorEither}
import com.mabrcosta.keysmanager.users.data.UsersGroup
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect.{left, right}

import scala.concurrent.ExecutionContext

class UsersGroupsServiceImpl[TDBIO[_], TDBOut[_]] @Inject()(
    private val usersGroups: DatabaseDal[UsersGroup, UUID, TDBIO],
    private val effectsDatabaseExecutor: EffectsDatabaseExecutor[TDBIO, TDBOut],
    implicit val executionContext: ExecutionContext)
    extends UsersGroupsService[TDBIO, TDBOut] {

  import effectsDatabaseExecutor._

  override def get[R: _tDBOut: _errorEither](uidUsersGroup: UUID): Eff[R, UsersGroup] = {
    for {
      usersGroupOpt <- usersGroups.find(uidUsersGroup).execute
      usersGroup <- if (usersGroupOpt.isDefined) right(usersGroupOpt.get)
      else left[R, Error, UsersGroup](NotFound(s"Unable to find users group for uid $uidUsersGroup"))
    } yield usersGroup
  }

}
