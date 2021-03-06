package com.mabrcosta.keysmanager.core.persistence

import java.time.Instant
import java.util.UUID

import com.byteslounge.slickrepo.meta.Entity
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.util.TypeMapper
import slick.ast.BaseTypedType
import slick.jdbc.{JdbcProfile, JdbcType}

abstract class BaseDBIORepository[TEntity <: Entity[TEntity, EntityId[TEntity]]](
    override val profile: JdbcProfile)
    extends SimpleDBIORepository[TEntity](profile) with TypeMapper {

  import profile.api._

  abstract class BaseRepositoryTable(tag: Tag, schema: Option[String] = None, name: String)
      extends SimpleRepositoryTable(tag, schema, name) {

    implicit val instantMapper: JdbcType[Instant] with BaseTypedType[Instant] = DateMapper.instant2SqlTimestampMapper

    def uidCreatorUser = column[Option[UUID]]("creator_subject_id")
    def uidLastModifierUser = column[Option[UUID]]("last_modifier_subject_id")
    def creationInstant = column[Instant]("creation_instant")
    def updateInstant = column[Instant]("update_instant")
  }

}
