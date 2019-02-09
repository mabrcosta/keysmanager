package com.mabrcosta.keysmanager.machines.persistence.func

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, DatabaseDal, PersistenceSchema}
import com.mabrcosta.keysmanager.machines.data.MachineAccessProvider
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

class MachinesAccessProvidersRepository @Inject()(private[this] val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[MachineAccessProvider](jdbcProfile)
    with DatabaseDal[MachineAccessProvider, EntityId[MachineAccessProvider], SlickDBIO] {

  import profile.api._

  type TableType = MachinesAccessProviders
  val tableQuery = TableQuery[MachinesAccessProviders]
  val pkType = implicitly[BaseTypedType[EntityId[MachineAccessProvider]]]

  class MachinesAccessProviders(tag: Tag)
      extends SimpleRepositoryTable(tag, Some(PersistenceSchema.schema), "machines_access_providers") {

    def * = id.? <> (MachineAccessProvider.apply, MachineAccessProvider.unapply)
  }

}
