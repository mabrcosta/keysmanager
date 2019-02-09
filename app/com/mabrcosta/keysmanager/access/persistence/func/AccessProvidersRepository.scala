package com.mabrcosta.keysmanager.access.persistence.func

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.access.data.AccessProvider
import com.mabrcosta.keysmanager.access.persistence.api.AccessProvidersDal
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, PersistenceSchema}
import com.mabrcosta.keysmanager.machines.data.MachineAccessProvider
import com.mabrcosta.keysmanager.users.data.UserAccessProvider
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.{JdbcProfile, JdbcType}

class AccessProvidersRepository @Inject()(private[this] val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[AccessProvider](jdbcProfile)
    with AccessProvidersDal[SlickDBIO] {

  import profile.api._

  type TableType = AccessProviders
  val tableQuery = TableQuery[AccessProviders]
  val pkType = implicitly[BaseTypedType[EntityId[AccessProvider]]]

  implicit val userAccessProviderIdMapper
  : JdbcType[EntityId[UserAccessProvider]] with BaseTypedType[EntityId[UserAccessProvider]] =
    IdMapper.entityIdMapper[UserAccessProvider]

  implicit val machineAccessProviderIdMapper
  : JdbcType[EntityId[MachineAccessProvider]] with BaseTypedType[EntityId[MachineAccessProvider]] =
    IdMapper.entityIdMapper[MachineAccessProvider]

  class AccessProviders(tag: Tag) extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "access_providers") {
    def userAccessProviderId = column[EntityId[UserAccessProvider]]("user_access_provider_id")
    def machineAccessProviderId = column[EntityId[MachineAccessProvider]]("machine_access_provider_id")
    def startInstant = column[Instant]("start_instant")
    def endInstant = column[Instant]("end_instant")

    def * =
      (id.?,
       userAccessProviderId,
       machineAccessProviderId,
       startInstant,
       endInstant,
       uidCreatorUser,
       uidLastModifierUser,
       creationInstant,
       updateInstant) <> (AccessProvider.tupled, AccessProvider.unapply)
  }

  def findForMachinesProviders(machinesProviderIds: Seq[EntityId[MachineAccessProvider]],
                               at: Instant): DBIO[Seq[AccessProvider]] = {
    implicit val dateMapper: JdbcType[Instant] with BaseTypedType[Instant] = DateMapper.instant2SqlTimestampMapper
    tableQuery
      .filter(_.machineAccessProviderId inSet machinesProviderIds)
      .filter(a => a.startInstant <= at && a.endInstant > at)
      .result
  }

}
