package com.mabrcosta.keysmanager.users.persistence.func

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, PersistenceSchema}
import com.mabrcosta.keysmanager.users.data.{UserAccessProvider, UsersGroup}
import com.mabrcosta.keysmanager.users.persistence.api.UsersGroupsDal
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.{JdbcProfile, JdbcType}

class UsersGroupsRepository @Inject()(private[this] val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[UsersGroup](jdbcProfile)
    with UsersGroupsDal[SlickDBIO] {

  import profile.api._

  type TableType = UsersGroups
  val tableQuery = TableQuery[UsersGroups]
  val pkType = implicitly[BaseTypedType[EntityId[UsersGroup]]]

  implicit val userAccessProviderIdMapper
  : JdbcType[EntityId[UserAccessProvider]] with BaseTypedType[EntityId[UserAccessProvider]] =
    IdMapper.entityIdMapper[UserAccessProvider]


  class UsersGroups(tag: Tag) extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "users_groups") {
    def name = column[String]("name")
    def uidUserAccessProvider = column[EntityId[UserAccessProvider]]("user_access_provider_id")

    def * =
      (id.?, name, uidUserAccessProvider, uidCreatorUser, uidLastModifierUser, creationInstant,
        updateInstant) <> (UsersGroup.tupled, UsersGroup.unapply)
  }

  def findForUserAccessProviders(usersAccessProviderIds: Seq[EntityId[UserAccessProvider]]): DBIO[Seq[UsersGroup]] = {
    tableQuery.filter(_.uidUserAccessProvider inSet usersAccessProviderIds).result
  }

}
