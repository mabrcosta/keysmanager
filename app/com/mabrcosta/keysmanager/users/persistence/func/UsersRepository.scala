package com.mabrcosta.keysmanager.users.persistence.func

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, PersistenceSchema}
import com.mabrcosta.keysmanager.users.data.User
import com.mabrcosta.keysmanager.users.persistence.api.UsersDal
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

class UsersRepository @Inject()(private[this] val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[User, UUID](jdbcProfile)
    with UsersDal[SlickDBIO] {

  import profile.api._

  type TableType = Users
  val tableQuery = TableQuery[Users]
  val pkType = implicitly[BaseTypedType[UUID]]

  class Users(tag: Tag) extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "users") {
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def uidUserAccessProvider = column[UUID]("user_access_provider_id")

    def * =
      (id.?, firstName, lastName, uidUserAccessProvider, uidCreatorUser, uidLastModifierUser, creationInstant,
        updateInstant) <> (User.tupled, User.unapply)
  }

  def findForUserAccessProviders(uidUsersAccessProviders: Seq[UUID]): DBIO[Seq[User]] = {
    tableQuery.filter(_.uidUserAccessProvider inSet uidUsersAccessProviders).result
  }

}
