package com.mabrcosta.keysmanager.users.key.persistence

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, DatabaseDal}
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

trait KeysDal[TIO[_]] extends DatabaseDal[Key, UUID, TIO] {
  def getForOwner(uidOwner: UUID): TIO[Seq[Key]]
}

class KeysRepository @Inject()(private val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[Key, UUID](jdbcProfile)
    with KeysDal[SlickDBIO] {

  import profile.api._

  type TableType = Keys
  val tableQuery = TableQuery[Keys]
  val pkType = implicitly[BaseTypedType[UUID]]

  class Keys(tag: Tag) extends BaseRepositoryTable(tag, None, "keys") {
    def value = column[String]("value")

    def ownerSubjectId = column[UUID]("owner_subject_id")

    def * =
      (id, value, ownerSubjectId, uidCreatorSubject, uidLastModifierSubject, creationTimestamp,
        updateTimestamp) <> (Key.tupled, Key.unapply)
  }

  def getForOwner(uidOwner: UUID): DBIO[Seq[Key]] = tableQuery.filter(_.ownerSubjectId === uidOwner).result

}
