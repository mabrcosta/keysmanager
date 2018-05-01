package com.mabrcosta.keysmanager.users.persistence

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, DatabaseDal, PersistenceSchema}
import com.mabrcosta.keysmanager.users.data.User
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

class UsersRepository @Inject()(private val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[User, UUID](jdbcProfile)
    with DatabaseDal[User, UUID, SlickDBIO] {

  import profile.api._

  type TableType = Users
  val tableQuery = TableQuery[Users]
  val pkType = implicitly[BaseTypedType[UUID]]

  class Users(tag: Tag) extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "users") {
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def uidUserAccessProvider = column[UUID]("uid_user_access_provider")

    def * =
      (id.?, firstName, lastName, uidUserAccessProvider, uidCreatorUser, uidLastModifierUser, creationInstant,
        updateInstant) <> (User.tupled, User.unapply)
  }

}
