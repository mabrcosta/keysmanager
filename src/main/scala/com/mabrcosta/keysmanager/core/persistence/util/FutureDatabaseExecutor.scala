package com.mabrcosta.keysmanager.core.persistence.util

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcBackend

import scala.concurrent.Future

class FutureDatabaseExecutor @Inject()(implicit backend: WithSessionBackend)
    extends LazyLogging {

  implicit def apply[T](action: SlickDBIO[T]): FutureDatabaseActionExecutor[T] =
    new FutureDatabaseActionExecutor[T](action)

  class FutureDatabaseActionExecutor[T](private val action: SlickDBIO[T])(implicit backend: WithSessionBackend) {
    import backend._

    def execute(implicit session: JdbcBackend#Session): Future[T] = session.run(action)
  }

}
