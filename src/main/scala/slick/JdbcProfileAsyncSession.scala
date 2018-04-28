package slick

import slick.jdbc.JdbcBackend

import scala.concurrent.{ExecutionContext, Future}

trait JdbcProfileAsyncSession {

  /**
    * Extends Session to add methods for session management.
    */
  implicit class AsyncSession(session: JdbcBackend#Session) {
    def withTransaction[T](f: => Future[T])(isSuccess: T => Boolean)(implicit ec: ExecutionContext): Future[T] = {
      val s = session.asInstanceOf[JdbcBackend#BaseSession]

      if (s.isInTransaction) f
      else {
        s.startInTransaction
        f.map(v => {
            if (isSuccess(v)) commit(s) else rollback(s)
            v
          })
          .recoverWith({
            case ex: Throwable => {
              rollback(s)
              Future.failed(ex)
            }
          })
      }
    }

    private def commit(session: JdbcBackend#BaseSession): Unit = session.endInTransaction(session.conn.commit())

    private def rollback(session: JdbcBackend#BaseSession): Unit = session.endInTransaction(session.conn.rollback())
  }
}
