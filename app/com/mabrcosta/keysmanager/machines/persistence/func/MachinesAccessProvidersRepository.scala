package com.mabrcosta.keysmanager.machines.persistence.func

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, DatabaseDal, PersistenceSchema}
import com.mabrcosta.keysmanager.machines.data.MachinesAccessProvider
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

class MachinesAccessProvidersRepository @Inject()(private val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[MachinesAccessProvider, UUID](jdbcProfile)
    with DatabaseDal[MachinesAccessProvider, UUID, SlickDBIO] {

  import profile.api._

  type TableType = MachinesAccessProviders
  val tableQuery = TableQuery[MachinesAccessProviders]
  val pkType = implicitly[BaseTypedType[UUID]]

  class MachinesAccessProviders(tag: Tag)
      extends SimpleRepositoryTable(tag, Some(PersistenceSchema.schema), "machines_access_providers") {

    def * = id.? <> (MachinesAccessProvider.apply, MachinesAccessProvider.unapply)
  }

}
