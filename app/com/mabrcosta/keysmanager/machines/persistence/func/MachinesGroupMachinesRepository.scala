package com.mabrcosta.keysmanager.machines.persistence.func

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, PersistenceSchema}
import com.mabrcosta.keysmanager.machines.data.{Machine, MachinesGroup, MachinesGroupMachine}
import com.mabrcosta.keysmanager.machines.persistence.api.MachinesGroupMachinesDal
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.{JdbcProfile, JdbcType}

class MachinesGroupMachinesRepository @Inject()(private[this] val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[MachinesGroupMachine](jdbcProfile)
    with MachinesGroupMachinesDal[SlickDBIO] {

  import profile.api._

  type TableType = MachinesGroupMachines
  val tableQuery = TableQuery[MachinesGroupMachines]
  val pkType = implicitly[BaseTypedType[EntityId[MachinesGroupMachine]]]

  implicit val machineIdMapper: JdbcType[EntityId[Machine]] with BaseTypedType[EntityId[Machine]] =
    IdMapper.entityIdMapper[Machine]
  implicit val machinesGroupIdMapper: JdbcType[EntityId[MachinesGroup]] with BaseTypedType[EntityId[MachinesGroup]] =
    IdMapper.entityIdMapper[MachinesGroup]

  class MachinesGroupMachines(tag: Tag)
      extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "machines_groups_machines") {
    def machineId = column[EntityId[Machine]]("machine_id")
    def machinesGroupId = column[EntityId[MachinesGroup]]("machines_group_id")

    def * =
      (id.?, machineId, machinesGroupId, uidCreatorUser, uidLastModifierUser, creationInstant,
        updateInstant) <> (MachinesGroupMachine.tupled, MachinesGroupMachine.unapply)
  }

  private[this] lazy val findForMachineCompiled = Compiled(
    (machineId: Rep[EntityId[Machine]]) => tableQuery.filter(_.machineId === machineId))

  def findForMachine(machineId: EntityId[Machine]): DBIO[Seq[MachinesGroupMachine]] =
    findForMachineCompiled(machineId).result

}
