package com.mabrcosta.keysmanager.machines.persistence

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, DatabaseDal, PersistenceSchema}
import com.mabrcosta.keysmanager.machines.data.Machine
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

class MachinesRepository @Inject()(private val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[Machine, UUID](jdbcProfile)
    with DatabaseDal[Machine, UUID, SlickDBIO] {

  import profile.api._

  type TableType = Machines
  val tableQuery = TableQuery[Machines]
  val pkType = implicitly[BaseTypedType[UUID]]

  class Machines(tag: Tag) extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "machines") {
    def name = column[String]("name")
    def hostname = column[String]("hostname")
    def uidMachineAccessProvider = column[UUID]("uid_machine_access_provider")

    def * =
      (id.?, name, hostname, uidMachineAccessProvider, uidCreatorUser, uidLastModifierUser, creationInstant,
        updateInstant) <> (Machine.tupled, Machine.unapply)
  }

}
