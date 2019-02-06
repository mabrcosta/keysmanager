package com.mabrcosta.keysmanager.machines.business

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.access.business.api.AccessService
import com.mabrcosta.keysmanager.core.business.api.{Error, NotFound}
import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.machines.business.api.{MachinesGroupsService, MachinesService}
import com.mabrcosta.keysmanager.machines.data.Machine
import com.mabrcosta.keysmanager.machines.persistence.api.MachinesDal
import com.mabrcosta.keysmanager.users.business.api._errorEither
import com.mabrcosta.keysmanager.users.data.Key
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect.{left, right}

import scala.concurrent.ExecutionContext

class MachinesServiceImpl[TDBIO[_], TDBOut[_]] @Inject()(
    private val machinesDal: MachinesDal[TDBIO],
    private val machinesGroupService: MachinesGroupsService[TDBIO, TDBOut],
    private val accessService: AccessService[TDBIO, TDBOut],
    private val effectsDatabaseExecutor: EffectsDatabaseExecutor[TDBIO, TDBOut],
    implicit val executionContext: ExecutionContext)
    extends MachinesService[TDBIO, TDBOut] {

  import effectsDatabaseExecutor._

  override def getAuthorizedKeys[R: _tDBOut: _errorEither](hostname: String): Eff[R, Seq[Key]] = {
    for {
      machineOpt <- machinesDal.findForHostname(hostname).execute
      machine <- if (machineOpt.isDefined) right(machineOpt.get)
      else left[R, Error, Machine](NotFound(s"Unable to find machine for hostname $hostname"))
      groups <- machinesGroupService.getWithMachine(machine.id.get)
      keys <- accessService.getAuthorizedKeys(
        groups.map(_.uidMachineAccessProvider) :+ machine.uidMachineAccessProvider,
        Instant.now())
    } yield keys
  }

  override def get[R: _tDBOut: _errorEither](uidMachine: UUID): Eff[R, Machine] = {
    for {
      machineOpt <- machinesDal.find(uidMachine).execute
      machine <- if (machineOpt.isDefined) right(machineOpt.get)
      else left[R, Error, Machine](NotFound(s"Unable to find machine for uid $uidMachine"))
    } yield machine
  }
}
