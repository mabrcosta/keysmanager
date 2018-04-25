package slick

import slick.jdbc.JdbcBackend

import scala.concurrent.{ExecutionContext, Future}

trait JdbcProfileAsyncSession {

  /**
    * Extends Session to add methods for session management.
    */
  implicit class AsyncSession(session: JdbcBackend#Session) {
    def withTransaction[T](f: => Future[T])(implicit ec: ExecutionContext): Future[T] = {
      val s = session.asInstanceOf[JdbcBackend#BaseSession]

      if (s.isInTransaction) f
      else {
        s.startInTransaction
        f.map(v => {
            s.endInTransaction(s.conn.commit())
            v
          })
          .recoverWith({
            case ex: Throwable => {
              s.endInTransaction(s.conn.rollback())
              Future.failed(ex)
            }
          })
      }
    }
  }
}
