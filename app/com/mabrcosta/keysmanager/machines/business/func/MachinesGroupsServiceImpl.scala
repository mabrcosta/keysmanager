package com.mabrcosta.keysmanager.machines.business.func

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.machines.business.api.{MachinesGroupNotFound, MachinesGroupsError, MachinesGroupsService, _machinesGroupsErrorEither}
import com.mabrcosta.keysmanager.machines.data.{Machine, MachinesGroup}
import com.mabrcosta.keysmanager.machines.persistence.api.{MachinesGroupMachinesDal, MachinesGroupsDal}
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect.{left, right}

import scala.concurrent.ExecutionContext

class MachinesGroupsServiceImpl[TIOIn[_], TIOOut[_]] @Inject()(
    private[this] val machinesGroups: MachinesGroupsDal[TIOIn],
    private[this] val machinesGroupMachinesDal: MachinesGroupMachinesDal[TIOIn],
    private[this] val effectsDatabaseExecutor: EffectsDatabaseExecutor[TIOIn, TIOOut],
    implicit val executionContext: ExecutionContext)
    extends MachinesGroupsService[TIOIn, TIOOut] {

  import effectsDatabaseExecutor._

  override def get[R: _TIOOut: _machinesGroupsErrorEither](
      machinesGroupId: EntityId[MachinesGroup]): Eff[R, MachinesGroup] = {
    for {
      machinesGroupOpt <- machinesGroups.find(machinesGroupId).execute
      machinesGroup <- if (machinesGroupOpt.isDefined) right(machinesGroupOpt.get)
      else
        left[R, MachinesGroupsError, MachinesGroup](
          MachinesGroupNotFound(s"Unable to find machine group for id $machinesGroupId"))
    } yield machinesGroup
  }

  override def getWithMachine[R: _TIOOut](machineId: EntityId[Machine]): Eff[R, Seq[MachinesGroup]] = {
    for {
      groupMachines <- machinesGroupMachinesDal.findForMachine(machineId).execute
      groups <- machinesGroups.find(groupMachines.map(_.machinesGroupId)).execute
    } yield groups
  }

}
