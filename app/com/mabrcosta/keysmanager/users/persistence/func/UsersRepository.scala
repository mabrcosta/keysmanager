package com.mabrcosta.keysmanager.users.persistence.func

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, PersistenceSchema}
import com.mabrcosta.keysmanager.users.data.{User, UserAccessProvider}
import com.mabrcosta.keysmanager.users.persistence.api.UsersDal
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.{JdbcProfile, JdbcType}

class UsersRepository @Inject()(private[this] val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[User](jdbcProfile)
    with UsersDal[SlickDBIO] {

  import profile.api._

  type TableType = Users
  val tableQuery = TableQuery[Users]
  val pkType = implicitly[BaseTypedType[EntityId[User]]]

  implicit val userAccessProviderIdMapper
    : JdbcType[EntityId[UserAccessProvider]] with BaseTypedType[EntityId[UserAccessProvider]] =
    IdMapper.entityIdMapper[UserAccessProvider]

  class Users(tag: Tag) extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "users") {
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def uidUserAccessProvider = column[EntityId[UserAccessProvider]]("user_access_provider_id")

    def * =
      (id.?,
       firstName,
       lastName,
       uidUserAccessProvider,
       uidCreatorUser,
       uidLastModifierUser,
       creationInstant,
       updateInstant) <> (User.tupled, User.unapply)
  }

  def findForUserAccessProviders(usersAccessProviderIds: Seq[EntityId[UserAccessProvider]]): DBIO[Seq[User]] = {
    tableQuery.filter(_.uidUserAccessProvider inSet usersAccessProviderIds).result
  }

}
