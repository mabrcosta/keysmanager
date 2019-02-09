package com.mabrcosta.keysmanager.core.persistence

import com.byteslounge.slickrepo.meta.{Entity, Keyed}
import com.byteslounge.slickrepo.repository.Repository
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.util.TypeMapper
import slick.ast.BaseTypedType
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.{JdbcProfile, JdbcType}

abstract class SimpleDBIORepository[TEntity <: Entity[TEntity, EntityId[TEntity]]](val profile: JdbcProfile)
    extends Repository[TEntity, EntityId[TEntity]](profile)
    with DatabaseDal[TEntity, EntityId[TEntity], SlickDBIO]
    with TypeMapper {

  import profile.api._

  implicit val entityIdMapper: JdbcType[EntityId[TEntity]] with BaseTypedType[EntityId[TEntity]] =
    IdMapper.entityIdMapper[TEntity]

  abstract class SimpleRepositoryTable(tag: Tag, schema: Option[String], name: String)
      extends Table[TEntity](tag, schema, name)
      with Keyed[EntityId[TEntity]] {
    def id = column[EntityId[TEntity]]("id", O.PrimaryKey)
  }

  private[this] lazy val existsCompiled = Compiled((id: Rep[EntityId[TEntity]]) => tableQuery.filter(_.id === id).exists)

  override def exists(id: EntityId[TEntity]): DBIO[Boolean] = {
    existsCompiled(id).result
  }

  override def find(id: EntityId[TEntity]): DBIO[Option[TEntity]] = {
    findOneCompiled(id).result.headOption
  }

  override def find(ids: Seq[EntityId[TEntity]]): DBIO[Seq[TEntity]] = {
    tableQuery.filter(_.id inSet ids).result
  }

}
