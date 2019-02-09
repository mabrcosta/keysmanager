package com.mabrcosta.keysmanager.users.persistence.func

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, DatabaseDal, PersistenceSchema}
import com.mabrcosta.keysmanager.users.data.UserAccessProvider
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

class UsersAccessProvidersRepository @Inject()(private[this] val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[UserAccessProvider](jdbcProfile)
    with DatabaseDal[UserAccessProvider, EntityId[UserAccessProvider], SlickDBIO] {

  import profile.api._

  type TableType = UsersAccessProviders
  val tableQuery = TableQuery[UsersAccessProviders]
  val pkType = implicitly[BaseTypedType[EntityId[UserAccessProvider]]]

  class UsersAccessProviders(tag: Tag)
      extends SimpleRepositoryTable(tag, Some(PersistenceSchema.schema), "users_access_providers") {

    def * = id.? <> (UserAccessProvider.apply, UserAccessProvider.unapply)
  }

}
