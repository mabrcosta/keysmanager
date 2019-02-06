package com.mabrcosta.keysmanager.core.persistence.util

import java.sql.Timestamp
import java.time._
import java.util.UUID

import com.mabrcosta.keysmanager.core.data.EntityId
import slick.ast.BaseTypedType
import slick.jdbc.{JdbcProfile, JdbcType}

trait TypeMapper {
  protected val driver: JdbcProfile
  import driver.api._

  object IdMapper {
    def entityIdMapper[T]: JdbcType[EntityId[T]] with BaseTypedType[EntityId[T]] =
      MappedColumnType.base[EntityId[T], UUID](_.value, uuid => EntityId[T](uuid))
  }

  object DateMapper {

    val instant2SqlTimestampMapper: JdbcType[Instant] with BaseTypedType[Instant] =
      MappedColumnType.base[Instant, java.sql.Timestamp](
        { instant =>
          Timestamp.valueOf(LocalDateTime.ofInstant(instant, ZoneId.of("UTC")))
        }, { sqlTimestamp =>
          ZonedDateTime.of(sqlTimestamp.toLocalDateTime, ZoneId.of("UTC")).toInstant
        }
      )

    val localDate2SqlTimestampMapper: JdbcType[LocalDate] with BaseTypedType[LocalDate] =
      MappedColumnType.base[LocalDate, java.sql.Timestamp]({ localDateTime =>
        Timestamp.valueOf(localDateTime.atStartOfDay())
      }, { sqlTimestamp =>
        sqlTimestamp.toLocalDateTime.toLocalDate
      })

    val localDateTime2SqlTimestampMapper: JdbcType[LocalDateTime] with BaseTypedType[LocalDateTime] =
      MappedColumnType.base[LocalDateTime, java.sql.Timestamp]({ localDateTime =>
        Timestamp.valueOf(localDateTime)
      }, { sqlTimestamp =>
        sqlTimestamp.toLocalDateTime
      })

  }

}
