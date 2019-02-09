package com.mabrcosta.keysmanager.users.persistence.func

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.{BaseDBIORepository, PersistenceSchema}
import com.mabrcosta.keysmanager.users.data.{Key, User}
import com.mabrcosta.keysmanager.users.persistence.api.KeysDal
import javax.inject.Inject
import slick.ast.BaseTypedType
import slick.ast.ColumnOption.Unique
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.{JdbcProfile, JdbcType}

class KeysRepository @Inject()(private[this] val jdbcProfile: JdbcProfile)
    extends BaseDBIORepository[Key](jdbcProfile)
    with KeysDal[SlickDBIO] {

  import profile.api._

  type TableType = Keys
  val tableQuery = TableQuery[Keys]
  val pkType = implicitly[BaseTypedType[EntityId[Key]]]

  implicit val userIdMapper: JdbcType[EntityId[User]] with BaseTypedType[EntityId[User]] =
    IdMapper.entityIdMapper[User]

  class Keys(tag: Tag) extends BaseRepositoryTable(tag, Some(PersistenceSchema.schema), "keys") {
    def value = column[String]("value", Unique)
    def ownerUserId = column[EntityId[User]]("owner_user_id")

    def * =
      (id.?, value, ownerUserId, uidCreatorUser, uidLastModifierUser, creationInstant,
        updateInstant) <> (Key.tupled, Key.unapply)
  }

  private[this] lazy val findForOwnerCompiled = Compiled(
    (userId: Rep[EntityId[User]]) => tableQuery.filter(_.ownerUserId === userId))

  def findForOwner(userId: EntityId[User]): DBIO[Seq[Key]] = findForOwnerCompiled(userId).result

  private[this] lazy val findForUIDAndOwnerCompiled = Compiled(
    (uid: Rep[EntityId[Key]], uidOwner: Rep[EntityId[User]]) =>
      tableQuery
        .filter(_.id === uid)
        .filter(_.ownerUserId === uidOwner))

  def findForOwner(keyId: EntityId[Key], userId: EntityId[User]): DBIO[Option[Key]] =
    findForUIDAndOwnerCompiled(keyId, userId).result.headOption

  def findForOwners(userIds: Seq[EntityId[User]]): DBIO[Seq[Key]] = {
    tableQuery.filter(_.ownerUserId inSet userIds).result
  }

}
