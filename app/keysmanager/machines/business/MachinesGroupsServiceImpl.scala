package com.mabrcosta.keysmanager.machines.business

import java.util.UUID

import com.mabrcosta.keysmanager.core.business.api.{Error, NotFound}
import com.mabrcosta.keysmanager.core.persistence.util.EffectsDatabaseExecutor
import com.mabrcosta.keysmanager.machines.business.api.MachinesGroupsService
import com.mabrcosta.keysmanager.machines.data.MachinesGroup
import com.mabrcosta.keysmanager.machines.persistence.api.{MachinesGroupsDal, MachinesGroupMachinesDal}
import com.mabrcosta.keysmanager.users.business.api._errorEither
import javax.inject.Inject
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect.{left, right}

import scala.concurrent.ExecutionContext

class MachinesGroupsServiceImpl[TDBIO[_], TDBOut[_]] @Inject()(
                                                                private val machinesGroups: MachinesGroupsDal[TDBIO],
                                                                private val machinesGroupMachinesDal: MachinesGroupMachinesDal[TDBIO],
                                                                private val effectsDatabaseExecutor: EffectsDatabaseExecutor[TDBIO, TDBOut],
                                                                implicit val executionContext: ExecutionContext)
    extends MachinesGroupsService[TDBIO, TDBOut] {

  import effectsDatabaseExecutor._

  override def getWithMachine[R: _tDBOut](uidMachine: UUID): Eff[R, Seq[MachinesGroup]] = {
    for {
      groupMachines <- machinesGroupMachinesDal.findForMachine(uidMachine).execute
      groups <- machinesGroups.find(groupMachines.map(_.uidMachinesGroup)).execute
    } yield groups
  }

  override def get[R: _tDBOut: _errorEither](uidMachinesGroup: UUID): Eff[R, MachinesGroup] = {
    for {
      machinesGroupOpt <- machinesGroups.find(uidMachinesGroup).execute
      machinesGroup <- if (machinesGroupOpt.isDefined) right(machinesGroupOpt.get)
      else left[R, Error, MachinesGroup](NotFound(s"Unable to find machine for uid $uidMachinesGroup"))
    } yield machinesGroup
  }

}
