package com.mabrcosta.keysmanager.core.persistence.util

import javax.inject.Inject
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class FutureDatabaseExecutor @Inject()(implicit val profile: JdbcProfile,
                                       implicit val db: JdbcProfile#Backend#Database) {

  implicit def apply[T](action: SlickDBIO[T]): FutureDatabaseActionExecutor[T] =
    new FutureDatabaseActionExecutor[T](action)

  class FutureDatabaseActionExecutor[T](private val action: SlickDBIO[T])(implicit db: JdbcProfile#Backend#Database,
                                                                          profile: JdbcProfile) {
    def execute: Future[T] = {
      import profile.api._
      db.run(action.transactionally)
    }
  }
}
