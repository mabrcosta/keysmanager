package com.mabrcosta.keysmanager.access.business.func

import java.time.Instant

import com.mabrcosta.keysmanager.access.business.api.{AccessError, AccessProviderNotFound, AccessService, _accessErrorEither}
import com.mabrcosta.keysmanager.access.data._
import com.mabrcosta.keysmanager.access.persistence.api.AccessProvidersDal
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.machines.business.api.{MachinesGroupsService, MachinesService, _machinesErrorEither, _machinesGroupsErrorEither}
import com.mabrcosta.keysmanager.machines.data.{Machine, MachineAccessProvider, MachinesGroup}
import com.mabrcosta.keysmanager.users.business.api._
import com.mabrcosta.keysmanager.users.data.{Key, User, UsersGroup}
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect.{left, right}

import scala.concurrent.ExecutionContext

class AccessServiceImpl[TIOIn[_], TIOOut[_]] @Inject()(
    private[this] val accessProvidersDal: AccessProvidersDal[TIOIn],
    private[this] val machinesService: MachinesService[TIOIn, TIOOut],
    private[this] val machinesGroupService: MachinesGroupsService[TIOIn, TIOOut],
    private[this] val usersService: UsersService[TIOIn, TIOOut],
    private[this] val usersGroupService: UsersGroupsService[TIOIn, TIOOut],
    private[this] val keysService: KeysService[TIOIn, TIOOut],
    private[this] val effectsDatabaseExecutor: EffectsDatabaseExecutor[TIOIn, TIOOut],
    implicit val executionContext: ExecutionContext)
    extends AccessService[TIOIn, TIOOut] {

  import effectsDatabaseExecutor._

  override def getAuthorizedKeys[R: _TIOOut: _machinesErrorEither](hostname: String): Eff[R, Seq[Key]] = {
    for {
      machine <- machinesService.getForHostname(hostname)
      groups <- machinesGroupService.getWithMachine(machine.id.get)
      keys <- getMachineProvidersAuthorizedKeys(
        groups.map(_.machineAccessProviderId) :+ machine.machineAccessProviderId,
        Instant.now())
    } yield keys
  }

  private[this] def getMachineProvidersAuthorizedKeys[R: _TIOOut](
      machineProviderIds: Seq[EntityId[MachineAccessProvider]],
      at: Instant): Eff[R, Seq[Key]] = {
    for {
      accessProviders <- accessProvidersDal.findForMachinesProviders(machineProviderIds, at).execute
      usersProviders = accessProviders.map(_.userAccessProviderId)
      users <- usersService.getWithProviders[R](usersProviders)
      groups <- usersGroupService.getWithProviders[R](usersProviders)
      groupsUsers <- usersGroupService.getUsers[R](groups.map(_.id.get))
      keys <- keysService.getForOwners[R](groupsUsers.map(_.userId) ++ users.map(_.id.get))
    } yield keys
  }

  private[this] def get[R: _TIOOut: _accessErrorEither](
      accessProviderId: EntityId[AccessProvider]): Eff[R, AccessProvider] = {
    for {
      providerOpt <- accessProvidersDal.find(accessProviderId).execute
      provider <- if (providerOpt.isDefined) right(providerOpt.get)
      else
        left[R, AccessError, AccessProvider](
          AccessProviderNotFound(s"Unable to find access provider for id $accessProviderId"))
    } yield provider
  }

  //TODO: Add creator
  override def add[
      R: _TIOOut: _machinesErrorEither: _machinesGroupsErrorEither: _usersErrorEither: _usersGroupsErrorEither](
      accessProviderData: AccessProviderCreationData): Eff[R, AccessProviderData] = {
    for {
      machinesProvider <- getMachinesAccessProvider[R](accessProviderData.machineAccess)
      uidMachinesProvider = machinesProvider.fold(_.machineAccessProviderId, _.machineAccessProviderId)
      usersProvider <- getUsersAccessProvider[R](accessProviderData.userAccess)
      uidUsersProvider = usersProvider.fold(_.userAccessProviderId, _.userAccessProviderId)
      accessProvider = AccessProvider(
        machineAccessProviderId = uidMachinesProvider,
        userAccessProviderId = uidUsersProvider,
        startInstant = accessProviderData.startInstant,
        endInstant = accessProviderData.endInstant
      )
      result <- accessProvidersDal.save(accessProvider).execute
    } yield AccessProviderData(result, usersProvider, machinesProvider)
  }

  private[this] def getMachinesAccessProvider[R: _TIOOut: _machinesErrorEither: _machinesGroupsErrorEither](
      machineAccess: Either[MachinesGroupAccessCreationData, MachineAccessCreationData])
    : Eff[R, Either[MachinesGroup, Machine]] = {
    machineAccess match {
      case Right(machine)      => machinesService.get[R](machine.machineId).map(Right(_))
      case Left(machinesGroup) => machinesGroupService.get[R](machinesGroup.machinesGroupId).map(Left(_))
    }
  }

  private[this] def getUsersAccessProvider[R: _TIOOut: _usersErrorEither: _usersGroupsErrorEither](
      userAccess: Either[UsersGroupAccessCreationData, UserAccessCreationData]): Eff[R, Either[UsersGroup, User]] = {
    userAccess match {
      case Right(user)      => usersService.get[R](user.userId).map(Right(_))
      case Left(usersGroup) => usersGroupService.get[R](usersGroup.usersGroupId).map(Left(_))
    }
  }

  override def delete[R: _TIOOut: _accessErrorEither](accessProviderId: EntityId[AccessProvider]): Eff[R, Boolean] = {
    for {
      provider <- get[R](accessProviderId)
      _ <- accessProvidersDal.delete(provider).execute
    } yield true
  }

}
