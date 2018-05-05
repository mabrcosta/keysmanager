package com.mabrcosta.keysmanager.access.business

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.access.business.api.AccessService
import com.mabrcosta.keysmanager.access.data._
import com.mabrcosta.keysmanager.access.persistence.api.AccessProvidersDal
import com.mabrcosta.keysmanager.core.business.api.{Error, NotFound}
import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.machines.business.api.{MachinesGroupsService, MachinesService}
import com.mabrcosta.keysmanager.users.business.api.{UsersGroupsService, UsersService, _errorEither}
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect.{left, right}

import scala.concurrent.ExecutionContext

class AccessServiceImpl[TDBIO[_], TDBOut[_]] @Inject()(
    private val accessProvidersDal: AccessProvidersDal[TDBIO],
    private val machinesService: MachinesService[TDBIO, TDBOut],
    private val machinesGroupService: MachinesGroupsService[TDBIO, TDBOut],
    private val usersService: UsersService[TDBIO, TDBOut],
    private val usersGroupService: UsersGroupsService[TDBIO, TDBOut],
    private val effectsDatabaseExecutor: EffectsDatabaseExecutor[TDBIO, TDBOut],
    implicit val executionContext: ExecutionContext)
    extends AccessService[TDBIO, TDBOut] {

  import effectsDatabaseExecutor._

  //TODO: Search method

  override def getForMachines[R: _tDBOut](uidMachineProviders: Seq[UUID], at: Instant): Eff[R, Seq[AccessProvider]] = {
    accessProvidersDal.getForMachinesProviders(uidMachineProviders, at).execute
  }

  private def get[R: _tDBOut: _errorEither](uidProvider: UUID): Eff[R, AccessProvider] = {
    for {
      providerOpt <- accessProvidersDal.find(uidProvider).execute
      provider <- if (providerOpt.isDefined) right(providerOpt.get)
      else left[R, Error, AccessProvider](NotFound(s"Unable to find access provider for uid $uidProvider"))
    } yield provider
  }

  //TODO: Add creator
  override def add[R: _tDBOut: _errorEither](accessProviderData: AccessProviderData): Eff[R, AccessProvider] = {
    for {
      uidMachinesProvider <- getMachinesAccessProviderId[R](accessProviderData.machineAccess)
      uidUsersProvider <- getUsersAccessProviderId[R](accessProviderData.userAccess)
      accessProvider = AccessProvider(
        uidMachineAccessProvider = uidMachinesProvider,
        uidUserAccessProvider = uidUsersProvider,
        startInstant = accessProviderData.startInstant,
        endInstant = accessProviderData.endInstant
      )
      result <- accessProvidersDal.save(accessProvider).execute
    } yield result
  }

  private def getMachinesAccessProviderId[R: _tDBOut: _errorEither](
      machineAccess: Either[MachinesGroupAccessData, MachineAccessData]): Eff[R, UUID] = {
    machineAccess match {
      case Right(machine)      => machinesService.get[R](machine.uid).map(_.uidMachineAccessProvider)
      case Left(machinesGroup) => machinesGroupService.get[R](machinesGroup.uid).map(_.uidMachineAccessProvider)
    }
  }

  private def getUsersAccessProviderId[R: _tDBOut: _errorEither](
      userAccess: Either[UsersGroupAccessData, UserAccessData]): Eff[R, UUID] = {
    userAccess match {
      case Right(user)      => usersService.get[R](user.uid).map(_.uidUserAccessProvider)
      case Left(usersGroup) => usersGroupService.get[R](usersGroup.uid).map(_.uidUserAccessProvider)
    }
  }

  override def delete[R: _tDBOut: _errorEither](uidProvider: UUID): Eff[R, Boolean] = {
    for {
      provider <- get[R](uidProvider)
      _ <- accessProvidersDal.delete(provider).execute
    } yield true
  }

}
