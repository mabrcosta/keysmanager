package com.mabrcosta.keysmanager.users.persistence

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, DatabaseDal, PersistenceSchema}
import com.mabrcosta.keysmanager.users.data.UsersGroup
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

class UsersGroupsRepository @Inject()(private val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[UsersGroup, UUID](jdbcProfile)
    with DatabaseDal[UsersGroup, UUID, SlickDBIO] {

  import profile.api._

  type TableType = UsersGroups
  val tableQuery = TableQuery[UsersGroups]
  val pkType = implicitly[BaseTypedType[UUID]]

  class UsersGroups(tag: Tag) extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "users_groups") {
    def name = column[String]("name")
    def uidUserAccessProvider = column[UUID]("uid_user_access_provider")

    def * =
      (id.?, name, uidUserAccessProvider, uidCreatorUser, uidLastModifierUser, creationInstant,
        updateInstant) <> (UsersGroup.tupled, UsersGroup.unapply)
  }

  def getForUserAccessProviders(uidUsersAccessProviders: Seq[UUID]): DBIO[Seq[UsersGroup]] = {
    tableQuery.filter(_.uidUserAccessProvider inSet uidUsersAccessProviders).result
  }

}
