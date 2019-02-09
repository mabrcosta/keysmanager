package com.mabrcosta.keysmanager.machines.persistence.func

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, PersistenceSchema}
import com.mabrcosta.keysmanager.machines.data.{Machine, MachineAccessProvider}
import com.mabrcosta.keysmanager.machines.persistence.api.MachinesDal
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.{JdbcProfile, JdbcType}

class MachinesRepository @Inject()(private[this] val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[Machine](jdbcProfile)
    with MachinesDal[SlickDBIO] {

  import profile.api._

  type TableType = Machines
  val tableQuery = TableQuery[Machines]
  val pkType = implicitly[BaseTypedType[EntityId[Machine]]]

  implicit val machineAccessProviderIdMapper
    : JdbcType[EntityId[MachineAccessProvider]] with BaseTypedType[EntityId[MachineAccessProvider]] =
    IdMapper.entityIdMapper[MachineAccessProvider]

  class Machines(tag: Tag) extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "machines") {
    def name = column[String]("name")
    def hostname = column[String]("hostname")
    def machineAccessProviderId = column[EntityId[MachineAccessProvider]]("machine_access_provider_id")

    def * =
      (id.?,
       name,
       hostname,
       machineAccessProviderId,
       uidCreatorUser,
       uidLastModifierUser,
       creationInstant,
       updateInstant) <> (Machine.tupled, Machine.unapply)
  }

  private[this] lazy val findForHostnameCompiled = Compiled(
    (hostname: Rep[String]) => tableQuery.filter(_.hostname === hostname))

  def findForHostname(hostname: String): DBIO[Option[Machine]] =
    findForHostnameCompiled(hostname).result.headOption

}
