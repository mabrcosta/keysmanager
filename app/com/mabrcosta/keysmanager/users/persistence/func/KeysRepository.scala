package com.mabrcosta.keysmanager.users.persistence.func

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, PersistenceSchema}
import com.mabrcosta.keysmanager.users.data.Key
import com.mabrcosta.keysmanager.users.persistence.api.KeysDal
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.ast.ColumnOption.Unique
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

class KeysRepository @Inject()(private val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[Key, UUID](jdbcProfile)
    with KeysDal[SlickDBIO] {

  import profile.api._

  type TableType = Keys
  val tableQuery = TableQuery[Keys]
  val pkType = implicitly[BaseTypedType[UUID]]

  class Keys(tag: Tag) extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "keys") {
    def value = column[String]("value", Unique)
    def uidOwnerSubject = column[UUID]("owner_user_id")

    def * =
      (id.?, value, uidOwnerSubject, uidCreatorUser, uidLastModifierUser, creationInstant,
        updateInstant) <> (Key.tupled, Key.unapply)
  }

  private lazy val findForOwnerCompiled = Compiled(
    (uidOwner: Rep[UUID]) => tableQuery.filter(_.uidOwnerSubject === uidOwner))

  def findForOwner(uidOwner: UUID): DBIO[Seq[Key]] = findForOwnerCompiled(uidOwner).result

  private lazy val findForUIDAndOwnerCompiled = Compiled(
    (uid: Rep[UUID], uidOwner: Rep[UUID]) =>
      tableQuery
        .filter(_.id === uid)
        .filter(_.uidOwnerSubject === uidOwner))

  def findForOwner(uid: UUID, uidOwner: UUID): DBIO[Option[Key]] =
    findForUIDAndOwnerCompiled(uid, uidOwner).result.headOption

  def findForOwners(uidOwners: Seq[UUID]): DBIO[Seq[Key]] = {
    tableQuery.filter(_.uidOwnerSubject inSet uidOwners).result
  }

}
