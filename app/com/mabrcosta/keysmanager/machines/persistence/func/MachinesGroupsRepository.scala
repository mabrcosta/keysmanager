package com.mabrcosta.keysmanager.machines.persistence.func

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, PersistenceSchema}
import com.mabrcosta.keysmanager.machines.data.{MachineAccessProvider, MachinesGroup}
import com.mabrcosta.keysmanager.machines.persistence.api.MachinesGroupsDal
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.{JdbcProfile, JdbcType}

class MachinesGroupsRepository @Inject()(private[this] val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[MachinesGroup](jdbcProfile)
    with MachinesGroupsDal[SlickDBIO] {

  import profile.api._

  type TableType = MachinesGroups
  val tableQuery = TableQuery[MachinesGroups]
  val pkType = implicitly[BaseTypedType[EntityId[MachinesGroup]]]

  implicit val machineAccessProviderIdMapper
    : JdbcType[EntityId[MachineAccessProvider]] with BaseTypedType[EntityId[MachineAccessProvider]] =
    IdMapper.entityIdMapper[MachineAccessProvider]

  class MachinesGroups(tag: Tag) extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "machines_groups") {
    def name = column[String]("name")
    def uidMachineAccessProvider = column[EntityId[MachineAccessProvider]]("machine_access_provider_id")

    def * =
      (id.?, name, uidMachineAccessProvider, uidCreatorUser, uidLastModifierUser, creationInstant,
        updateInstant) <> (MachinesGroup.tupled, MachinesGroup.unapply)
  }

}
