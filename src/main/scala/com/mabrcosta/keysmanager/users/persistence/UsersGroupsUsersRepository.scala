package com.mabrcosta.keysmanager.users.persistence

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, DatabaseDal, PersistenceSchema}
import com.mabrcosta.keysmanager.users.data.UsersGroupUser
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

class UsersGroupsUsersRepository @Inject()(private val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[UsersGroupUser, UUID](jdbcProfile)
    with DatabaseDal[UsersGroupUser, UUID, SlickDBIO] {

  import profile.api._

  type TableType = UsersGroupUsers
  val tableQuery = TableQuery[UsersGroupUsers]
  val pkType = implicitly[BaseTypedType[UUID]]

  class UsersGroupUsers(tag: Tag) extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "users_groups_user") {
    def uidUser = column[UUID]("uid_user")
    def uidUsersGroup = column[UUID]("uid_users_group")

    def * =
      (id.?, uidUser, uidUsersGroup, uidCreatorUser, uidLastModifierUser, creationInstant,
        updateInstant) <> (UsersGroupUser.tupled, UsersGroupUser.unapply)
  }

}
