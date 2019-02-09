package com.mabrcosta.keysmanager.users.persistence.func

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, PersistenceSchema}
import com.mabrcosta.keysmanager.users.data.{User, UsersGroup, UsersGroupUser}
import com.mabrcosta.keysmanager.users.persistence.api.UsersGroupsUsersDal
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.{JdbcProfile, JdbcType}

class UsersGroupsUsersRepository @Inject()(private[this] val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[UsersGroupUser](jdbcProfile)
    with UsersGroupsUsersDal[SlickDBIO] {

  import profile.api._

  type TableType = UsersGroupUsers
  val tableQuery = TableQuery[UsersGroupUsers]
  val pkType = implicitly[BaseTypedType[EntityId[UsersGroupUser]]]

  implicit val userIdMapper: JdbcType[EntityId[User]] with BaseTypedType[EntityId[User]] =
    IdMapper.entityIdMapper[User]
  implicit val usersGroupIdMapper: JdbcType[EntityId[UsersGroup]] with BaseTypedType[EntityId[UsersGroup]] =
    IdMapper.entityIdMapper[UsersGroup]

  class UsersGroupUsers(tag: Tag)
      extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "users_groups_user") {
    def userId = column[EntityId[User]]("user_id")
    def usersGroupId = column[EntityId[UsersGroup]]("users_group_id")

    def * =
      (id.?, userId, usersGroupId, uidCreatorUser, uidLastModifierUser, creationInstant,
        updateInstant) <> (UsersGroupUser.tupled, UsersGroupUser.unapply)
  }

  def findForUserGroups(usersGroupIds: Seq[EntityId[UsersGroup]]): DBIO[Seq[UsersGroupUser]] = {
    tableQuery.filter(_.usersGroupId inSet usersGroupIds).result
  }

}
