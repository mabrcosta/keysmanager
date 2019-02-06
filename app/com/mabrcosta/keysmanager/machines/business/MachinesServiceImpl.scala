package com.mabrcosta.keysmanager.machines.business

import java.util.UUID

import com.mabrcosta.keysmanager.core.business.api.{Error, NotFound}
import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.machines.business.api.{MachinesGroupsService, MachinesService}
import com.mabrcosta.keysmanager.machines.data.Machine
import com.mabrcosta.keysmanager.machines.persistence.api.MachinesDal
import com.mabrcosta.keysmanager.users.business.api._errorEither
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect.{left, right}

import scala.concurrent.ExecutionContext

class MachinesServiceImpl[TDBIO[_], TDBOut[_]] @Inject()(
    private val machinesDal: MachinesDal[TDBIO],
    private val machinesGroupService: MachinesGroupsService[TDBIO, TDBOut],
    private val effectsDatabaseExecutor: EffectsDatabaseExecutor[TDBIO, TDBOut],
    implicit val executionContext: ExecutionContext)
    extends MachinesService[TDBIO, TDBOut] {

  import effectsDatabaseExecutor._

  override def getForHostname[R: _tDBOut: _errorEither](hostname: String): Eff[R, Machine] = {
    handleOptionalMachine(machinesDal.findForHostname(hostname).execute,
                          s"Unable to find machine for hostname: $hostname")
  }

  override def get[R: _tDBOut: _errorEither](uidMachine: UUID): Eff[R, Machine] = {
    handleOptionalMachine(machinesDal.find(uidMachine).execute, s"Unable to find machine for uid $uidMachine")
  }

  private[this] def handleOptionalMachine[R: _errorEither](machineEffProvider: => Eff[R, Option[Machine]],
                                                           errorMessage: => String): Eff[R, Machine] = {
    for {
      machineOpt <- machineEffProvider
      machine <- if (machineOpt.isDefined) right(machineOpt.get)
      else left[R, Error, Machine](NotFound(errorMessage))
    } yield machine
  }

}
