package com.mabrcosta.keysmanager.core.persistence

import java.time.Instant
import java.util.UUID

import com.byteslounge.slickrepo.meta.Entity
import com.mabrcosta.keysmanager.core.persistence.util.DateMapper
import slick.ast.BaseTypedType
import slick.jdbc.{JdbcProfile, JdbcType}

abstract class BaseDBIORepository[TEntity <: Entity[TEntity, TKey], TKey](
    override val profile: JdbcProfile)
    extends SimpleDBIORepository[TEntity, TKey](profile) with DateMapper {

  import profile.api._

  abstract class BaseRepositoryTable(tag: Tag, schema: Option[String] = None, name: String)
      extends SimpleRepositoryTable(tag, schema, name) {

    implicit val instantMapper: JdbcType[Instant] with BaseTypedType[Instant] = DateMapper.instant2SqlTimestampMapper

    def uidCreatorUser = column[Option[UUID]]("uid_creator_user")
    def uidLastModifierUser = column[Option[UUID]]("uid_last_modifier_user")
    def creationInstant = column[Instant]("creation_instant")
    def updateInstant = column[Instant]("update_instant")
  }

}
