package com.mabrcosta.keysmanager.core.persistence

import java.sql.Timestamp
import java.time._

import slick.jdbc.JdbcProfile

trait DateMapper {
  implicit val profile: JdbcProfile
  import profile.api._

  object DateMapper {

    val utilDate2SqlTimestampMapper = MappedColumnType.base[java.util.Date, java.sql.Timestamp](
      { utilDate => new java.sql.Timestamp(utilDate.getTime()) },
      { sqlTimestamp => new java.util.Date(sqlTimestamp.getTime()) })

    val utilDate2SqlDate = MappedColumnType.base[java.util.Date, java.sql.Date](
      { utilDate => new java.sql.Date(utilDate.getTime()) },
      { sqlDate => new java.util.Date(sqlDate.getTime()) })

    val localDateTime2SqlTimestampMapper =  MappedColumnType.base[LocalDateTime, java.sql.Timestamp](
      { localDateTime => Timestamp.valueOf(localDateTime) },
      { sqlTimestamp => sqlTimestamp.toLocalDateTime})

    val localDate2SqlTimestampMapper =  MappedColumnType.base[LocalDate, java.sql.Timestamp](
      { localDateTime => Timestamp.valueOf(localDateTime.atStartOfDay()) },
      { sqlTimestamp => sqlTimestamp.toLocalDateTime.toLocalDate})

    val instant2SqlTimestampMapper =  MappedColumnType.base[Instant, java.sql.Timestamp](
      { instant => Timestamp.valueOf(LocalDateTime.ofInstant(instant, ZoneId.of("UTC")))},
      { sqlTimestamp => ZonedDateTime.of(sqlTimestamp.toLocalDateTime, ZoneId.of("UTC")).toInstant})

  }
}
