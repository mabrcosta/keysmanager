package com.mabrcosta.keysmanager.machines.business.func

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.machines.business.api._
import com.mabrcosta.keysmanager.machines.data.Machine
import com.mabrcosta.keysmanager.machines.persistence.api.MachinesDal
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect.{left, right}

import scala.concurrent.ExecutionContext

class MachinesServiceImpl[TIOIn[_], TIOOut[_]] @Inject()(
    private[this] val machinesDal: MachinesDal[TIOIn],
    private[this] val machinesGroupService: MachinesGroupsService[TIOIn, TIOOut],
    private[this] val effectsDatabaseExecutor: EffectsDatabaseExecutor[TIOIn, TIOOut],
    implicit val executionContext: ExecutionContext)
    extends MachinesService[TIOIn, TIOOut] {

  import effectsDatabaseExecutor._

  override def get[R: _TIOOut: _machinesErrorEither](machineId: EntityId[Machine]): Eff[R, Machine] = {
    handleOptionalMachine(machinesDal.find(machineId).execute, s"Unable to find machine for id $machineId")
  }

  override def getForHostname[R: _TIOOut: _machinesErrorEither](hostname: String): Eff[R, Machine] = {
    handleOptionalMachine(machinesDal.findForHostname(hostname).execute,
                          s"Unable to find machine for hostname: $hostname")
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
