package com.mabrcosta.keysmanager.users.business.func

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.users.business.api.{UsersGroupNotFound, UsersGroupsError, UsersGroupsService, _usersGroupsErrorEither}
import com.mabrcosta.keysmanager.users.data.{UserAccessProvider, UsersGroup, UsersGroupUser}
import com.mabrcosta.keysmanager.users.persistence.api.{UsersGroupsDal, UsersGroupsUsersDal}
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect.{left, right}

import scala.concurrent.ExecutionContext

class UsersGroupsServiceImpl[TIOIn[_], TIOOut[_]] @Inject()(
    private[this] val usersGroupsDal: UsersGroupsDal[TIOIn],
    private[this] val usersGroupsUsersDal: UsersGroupsUsersDal[TIOIn],
    private[this] val effectsDatabaseExecutor: EffectsDatabaseExecutor[TIOIn, TIOOut],
    implicit val executionContext: ExecutionContext)
    extends UsersGroupsService[TIOIn, TIOOut] {

  import effectsDatabaseExecutor._

  override def get[R: _TIOOut: _usersGroupsErrorEither](usersGroupId: EntityId[UsersGroup]): Eff[R, UsersGroup] = {
    for {
      usersGroupOpt <- usersGroupsDal.find(usersGroupId).execute
      usersGroup <- if (usersGroupOpt.isDefined) right(usersGroupOpt.get)
      else
        left[R, UsersGroupsError, UsersGroup](UsersGroupNotFound(s"Unable to find users group for id $usersGroupId"))
    } yield usersGroup
  }

  override def getWithProviders[R: _TIOOut](
      userAccessProviderIds: Seq[EntityId[UserAccessProvider]]): Eff[R, Seq[UsersGroup]] = {
    usersGroupsDal.findForUserAccessProviders(userAccessProviderIds).execute
  }

  override def getUsers[R: _TIOOut](usersGroupIds: Seq[EntityId[UsersGroup]]): Eff[R, Seq[UsersGroupUser]] = {
    usersGroupsUsersDal.findForUserGroups(usersGroupIds).execute
  }
}
