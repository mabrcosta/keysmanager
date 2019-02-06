package com.mabrcosta.keysmanager.users.persistence.func

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, DatabaseDal, PersistenceSchema}
import com.mabrcosta.keysmanager.users.data.UsersAccessProvider
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

class UsersAccessProvidersRepository @Inject()(private val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[UsersAccessProvider, UUID](jdbcProfile)
    with DatabaseDal[UsersAccessProvider, UUID, SlickDBIO] {

  import profile.api._

  type TableType = UsersAccessProviders
  val tableQuery = TableQuery[UsersAccessProviders]
  val pkType = implicitly[BaseTypedType[UUID]]

  class UsersAccessProviders(tag: Tag)
      extends SimpleRepositoryTable(tag, Some(PersistenceSchema.schema), "users_access_providers") {

    def * = id.? <> (UsersAccessProvider.apply, UsersAccessProvider.unapply)
  }

}
