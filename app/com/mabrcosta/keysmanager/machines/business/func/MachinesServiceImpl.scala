package com.mabrcosta.keysmanager.machines.business.func

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.machines.business.api._
import com.mabrcosta.keysmanager.machines.data.Machine
import com.mabrcosta.keysmanager.machines.persistence.api.MachinesDal
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

  override def getForHostname[R: _tDBOut: _machinesErrorEither](hostname: String): Eff[R, Machine] = {
    handleOptionalMachine(machinesDal.findForHostname(hostname).execute,
                          s"Unable to find machine for hostname: $hostname")
  }

  override def get[R: _tDBOut: _machinesErrorEither](uidMachine: UUID): Eff[R, Machine] = {
    handleOptionalMachine(machinesDal.find(uidMachine).execute, s"Unable to find machine for uid $uidMachine")
  }

  private[this] def handleOptionalMachine[R: _machinesErrorEither](machineEffProvider: => Eff[R, Option[Machine]],
                                                                   errorMessage: => String): Eff[R, Machine] = {
    for {
      machineOpt <- machineEffProvider
      machine <- if (machineOpt.isDefined) right(machineOpt.get)
      else left[R, MachinesError, Machine](MachineNotFound(errorMessage))
    } yield machine
  }

}
