package com.mabrcosta.keysmanager.core.persistence.util

import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

object FutureDatabaseExecutor {

  implicit def apply[T](action: SlickDBIO[T])(implicit db: JdbcProfile#Backend#Database,
                                              profile: JdbcProfile): FutureDatabaseExecutor[T] =
    new FutureDatabaseExecutor[T](action)

  class FutureDatabaseExecutor[T](private val action: SlickDBIO[T])(implicit db: JdbcProfile#Backend#Database,
                                                                    profile: JdbcProfile) {
    def execute: Future[T] = {
      import profile.api._
      db.run(action.transactionally)
    }
  }
}


