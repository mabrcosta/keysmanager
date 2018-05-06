package com.mabrcosta.keysmanager.access.persistence

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.access.data.AccessProvider
import com.mabrcosta.keysmanager.access.persistence.api.AccessProvidersDal
import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, DatabaseDal, PersistenceSchema}
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.{JdbcProfile, JdbcType}

class AccessProvidersRepository @Inject()(private val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[AccessProvider, UUID](jdbcProfile)
    with AccessProvidersDal[SlickDBIO] {

  import profile.api._

  type TableType = Users
  val tableQuery = TableQuery[Users]
  val pkType = implicitly[BaseTypedType[UUID]]

  class Users(tag: Tag) extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "users") {
    def uidUserAccessProvider = column[UUID]("uid_user_access_provider")
    def uidMachineAccessProvider = column[UUID]("uid_machine_access_provider")
    def startInstant = column[Instant]("start_instant")
    def endInstant = column[Instant]("end_instant")

    def * =
      (id.?,
       uidUserAccessProvider,
       uidMachineAccessProvider,
       startInstant,
       endInstant,
       uidCreatorUser,
       uidLastModifierUser,
       creationInstant,
       updateInstant) <> (AccessProvider.tupled, AccessProvider.unapply)
  }

  def findForMachinesProviders(uidMachinesProviders: Seq[UUID], at: Instant): DBIO[Seq[AccessProvider]] = {
    implicit val dateMapper: JdbcType[Instant] with BaseTypedType[Instant] = DateMapper.instant2SqlTimestampMapper
    tableQuery
      .filter(_.uidMachineAccessProvider inSet uidMachinesProviders)
      .filter(a => a.startInstant >= at && a.endInstant < at)
      .result
  }

}
