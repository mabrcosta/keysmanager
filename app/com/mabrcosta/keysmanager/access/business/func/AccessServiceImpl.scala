package com.mabrcosta.keysmanager.access.business.func

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.access.business.api.{AccessError, AccessProviderNotFound, AccessService, _accessErrorEither}
import com.mabrcosta.keysmanager.access.data._
import com.mabrcosta.keysmanager.access.persistence.api.AccessProvidersDal
import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.machines.business.api.{MachinesGroupsService, MachinesService, _machinesErrorEither, _machinesGroupsErrorEither}
import com.mabrcosta.keysmanager.machines.data.{Machine, MachinesGroup}
import com.mabrcosta.keysmanager.users.business.api._
import com.mabrcosta.keysmanager.users.data.{Key, User, UsersGroup}
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect.{left, right}

import scala.concurrent.ExecutionContext

class AccessServiceImpl[TDBIO[_], TDBOut[_]] @Inject()(
    private[this] val accessProvidersDal: AccessProvidersDal[TDBIO],
    private[this] val machinesService: MachinesService[TDBIO, TDBOut],
    private[this] val machinesGroupService: MachinesGroupsService[TDBIO, TDBOut],
    private[this] val usersService: UsersService[TDBIO, TDBOut],
    private[this] val usersGroupService: UsersGroupsService[TDBIO, TDBOut],
    private[this] val keysService: KeysService[TDBIO, TDBOut],
    private[this] val effectsDatabaseExecutor: EffectsDatabaseExecutor[TDBIO, TDBOut],
    implicit val executionContext: ExecutionContext)
    extends AccessService[TDBIO, TDBOut] {

  import effectsDatabaseExecutor._

  override def getAuthorizedKeys[R: _tDBOut: _machinesErrorEither](hostname: String): Eff[R, Seq[Key]] = {
    for {
      machine <- machinesService.getForHostname(hostname)
      groups <- machinesGroupService.getWithMachine(machine.id.get)
      keys <- getMachineProvidersAuthorizedKeys(
        groups.map(_.uidMachineAccessProvider) :+ machine.uidMachineAccessProvider,
        Instant.now())
    } yield keys
  }

  private[this] def getMachineProvidersAuthorizedKeys[R: _tDBOut](uidMachineProviders: Seq[UUID],
                                                                  at: Instant): Eff[R, Seq[Key]] = {
    for {
      accessProviders <- accessProvidersDal.findForMachinesProviders(uidMachineProviders, at).execute
      usersProviders = accessProviders.map(_.uidUserAccessProvider)
      users <- usersService.getWithProviders[R](usersProviders)
      groups <- usersGroupService.getWithProviders[R](usersProviders)
      groupsUsers <- usersGroupService.getUsers[R](groups.map(_.id.get))
      keys <- keysService.getForOwners[R](groupsUsers.map(_.uidUser) ++ users.map(_.id.get))
    } yield keys
  }

  private[this] def get[R: _tDBOut: _accessErrorEither](uidProvider: UUID): Eff[R, AccessProvider] = {
    for {
      providerOpt <- accessProvidersDal.find(uidProvider).execute
      provider <- if (providerOpt.isDefined) right(providerOpt.get)
      else
        left[R, AccessError, AccessProvider](
          AccessProviderNotFound(s"Unable to find access provider for uid $uidProvider"))
    } yield provider
  }

  //TODO: Add creator
  override def add[
      R: _tDBOut: _machinesErrorEither: _machinesGroupsErrorEither: _usersErrorEither: _usersGroupsErrorEither](
      accessProviderData: AccessProviderCreationData): Eff[R, AccessProviderData] = {
    for {
      machinesProvider <- getMachinesAccessProvider[R](accessProviderData.machineAccess)
      uidMachinesProvider = machinesProvider.fold(_.uidMachineAccessProvider, _.uidMachineAccessProvider)
      usersProvider <- getUsersAccessProvider[R](accessProviderData.userAccess)
      uidUsersProvider = usersProvider.fold(_.uidUserAccessProvider, _.uidUserAccessProvider)
      accessProvider = AccessProvider(
        uidMachineAccessProvider = uidMachinesProvider,
        uidUserAccessProvider = uidUsersProvider,
        startInstant = accessProviderData.startInstant,
        endInstant = accessProviderData.endInstant
      )
      result <- accessProvidersDal.save(accessProvider).execute
    } yield AccessProviderData(result, usersProvider, machinesProvider)
  }

  private[this] def getMachinesAccessProvider[R: _tDBOut: _machinesErrorEither: _machinesGroupsErrorEither](
      machineAccess: Either[MachinesGroupAccessCreationData, MachineAccessCreationData])
    : Eff[R, Either[MachinesGroup, Machine]] = {
    machineAccess match {
      case Right(machine)      => machinesService.get[R](machine.uid).map(Right(_))
      case Left(machinesGroup) => machinesGroupService.get[R](machinesGroup.uid).map(Left(_))
    }
  }

  private[this] def getUsersAccessProvider[R: _tDBOut: _usersErrorEither: _usersGroupsErrorEither](
      userAccess: Either[UsersGroupAccessCreationData, UserAccessCreationData]): Eff[R, Either[UsersGroup, User]] = {
    userAccess match {
      case Right(user)      => usersService.get[R](user.uid).map(Right(_))
      case Left(usersGroup) => usersGroupService.get[R](usersGroup.uid).map(Left(_))
    }
  }

  override def delete[R: _tDBOut: _accessErrorEither](uidProvider: UUID): Eff[R, Boolean] = {
    for {
      provider <- get[R](uidProvider)
      _ <- accessProvidersDal.delete(provider).execute
    } yield true
  }

}
