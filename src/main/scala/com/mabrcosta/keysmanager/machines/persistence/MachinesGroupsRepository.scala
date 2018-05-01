package com.mabrcosta.keysmanager.machines.persistence

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, DatabaseDal, PersistenceSchema}
import com.mabrcosta.keysmanager.machines.data.MachinesGroup
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

class MachinesGroupsRepository @Inject()(private val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[MachinesGroup, UUID](jdbcProfile)
    with DatabaseDal[MachinesGroup, UUID, SlickDBIO] {

  import profile.api._

  type TableType = MachinesGroups
  val tableQuery = TableQuery[MachinesGroups]
  val pkType = implicitly[BaseTypedType[UUID]]

  class MachinesGroups(tag: Tag) extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "machines_groups") {
    def name = column[String]("name")
    def uidMachineAccessProvider = column[UUID]("uid_machine_access_provider")

    def * =
      (id.?, name, uidMachineAccessProvider, uidCreatorUser, uidLastModifierUser, creationInstant,
        updateInstant) <> (MachinesGroup.tupled, MachinesGroup.unapply)
  }

}
