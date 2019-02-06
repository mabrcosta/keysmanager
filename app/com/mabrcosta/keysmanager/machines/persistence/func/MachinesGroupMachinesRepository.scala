package com.mabrcosta.keysmanager.machines.persistence.func

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, PersistenceSchema}
import com.mabrcosta.keysmanager.machines.data.MachinesGroupMachine
import com.mabrcosta.keysmanager.machines.persistence.api.MachinesGroupMachinesDal
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

class MachinesGroupMachinesRepository @Inject()(private val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[MachinesGroupMachine, UUID](jdbcProfile)
    with MachinesGroupMachinesDal[SlickDBIO] {

  import profile.api._

  type TableType = MachinesGroupMachines
  val tableQuery = TableQuery[MachinesGroupMachines]
  val pkType = implicitly[BaseTypedType[UUID]]

  class MachinesGroupMachines(tag: Tag)
      extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "machines_groups_machines") {
    def uidMachine = column[UUID]("machine_id")
    def uidMachinesGroup = column[UUID]("machines_group_id")

    def * = (id.?, uidMachine, uidMachinesGroup, uidCreatorUser, uidLastModifierUser, creationInstant,
      updateInstant) <> (MachinesGroupMachine.tupled, MachinesGroupMachine.unapply)
  }

  private lazy val findForMachineCompiled = Compiled(
    (uidMachine: Rep[UUID]) => tableQuery.filter(_.uidMachine === uidMachine))

  def findForMachine(uidMachine: UUID): DBIO[Seq[MachinesGroupMachine]] = findForMachineCompiled(uidMachine).result

}
