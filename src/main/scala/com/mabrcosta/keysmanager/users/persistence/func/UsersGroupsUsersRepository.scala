package com.mabrcosta.keysmanager.users.persistence.func

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, PersistenceSchema}
import com.mabrcosta.keysmanager.users.data.UsersGroupUser
import com.mabrcosta.keysmanager.users.persistence.api.UsersGroupsUsersDal
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

class UsersGroupsUsersRepository @Inject()(private val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[UsersGroupUser, UUID](jdbcProfile)
    with UsersGroupsUsersDal[SlickDBIO] {

  import profile.api._

  type TableType = UsersGroupUsers
  val tableQuery = TableQuery[UsersGroupUsers]
  val pkType = implicitly[BaseTypedType[UUID]]

  class UsersGroupUsers(tag: Tag)
      extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "users_groups_user") {
    def uidUser = column[UUID]("user_id")
    def uidUsersGroup = column[UUID]("users_group_id")

    def * = (id.?, uidUser, uidUsersGroup, uidCreatorUser, uidLastModifierUser, creationInstant,
        updateInstant) <> (UsersGroupUser.tupled, UsersGroupUser.unapply)
  }

  def findForUserGroups(uidUsersGroups: Seq[UUID]): DBIO[Seq[UsersGroupUser]] = {
    tableQuery.filter(_.uidUsersGroup inSet uidUsersGroups).result
  }

}
