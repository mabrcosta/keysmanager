package com.mabrcosta.keysmanager.core.persistence.util

import java.sql.Connection

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import slick.JdbcProfileAsyncSession
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.{JdbcBackend, JdbcProfile}

import scala.concurrent.{ExecutionContext, Future}

class FutureDatabaseExecutor @Inject()(implicit val profile: JdbcProfile,
                                       implicit val db: JdbcProfile#Backend#Database) extends LazyLogging {

  implicit def apply[T](action: SlickDBIO[T]): FutureDatabaseActionExecutor[T] =
    new FutureDatabaseActionExecutor[T](action)

  def withTransaction[T](f: (JdbcBackend#Session) => T): T = {
    new BlockingDatabase(db).withTransaction(f)
  }

  def withFutureTransaction[T](f: (JdbcBackend#Session) => Future[T]): Future[T] = {
    new BlockingDatabase(db).withFutureTransaction(f)
  }

  private class BlockingDatabase(db: JdbcBackend#Database) extends JdbcProfileAsyncSession {
//    import com.github.takezoe.slick.blocking.BlockingH2Driver.blockingApi._

    implicit val executionContext: ExecutionContext = db.executor.executionContext

    def withSession[T](f: (JdbcBackend#Session) => T): T = {
      val session = db.createSession()
      try {
        f(session)
      } finally {
        logger.info("CLOSING SESSION")
        session.close()
      }
    }

    def withTransaction[T](f: (JdbcBackend#Session) => T): T =
      withSession { s =>
        import com.github.takezoe.slick.blocking.BlockingH2Driver.blockingApi._
        s.withTransaction(f(s))
      }

    def withFutureSession[T](f: (JdbcBackend#Session) => Future[T]): Future[T] = {
      val session = db.createSession()
        val res = f(session)
        res.onComplete( _ => {
          logger.info("CLOSING SESSION")
          session.close()
        })
        res
    }

    def withFutureTransaction[T](f: (JdbcBackend#Session) => Future[T]): Future[T] =
      withFutureSession { s =>
        s.withTransaction(f(s))
      }
  }

  class FutureDatabaseActionExecutor[T](private val action: SlickDBIO[T])(implicit db: JdbcProfile#Backend#Database,
                                                                          profile: JdbcProfile) {
//    def execute: Future[T] = {
//      import profile.api._
//      db.run(action.transactionally)
//    }

    def execute(implicit session: JdbcBackend#Session): Future[T] = {
      session.run(action)
    }

    implicit private def withSession(session: JdbcBackend#Session): WithSessionJdbcBackend.WithSessionDatabaseDef = {
      WithSessionJdbcBackend(db, session)
    }

    object WithSessionJdbcBackend extends JdbcBackend {

      class WithSessionDatabaseDef(private val db: JdbcProfile#Backend#Database,
                                   private val session: JdbcBackend#Session)
          extends DatabaseDef(db.source, db.executor) {

        private val dbSession = session.asInstanceOf[Session]

        override def createSession(): Session = dbSession

        override protected[this] def createDatabaseActionContext[T](_useSameThread: Boolean): Context = {
          val ctx = new BlockingJdbcActionContext(dbSession)
          ctx.pin
          ctx
        }

        private class BlockingJdbcActionContext(s: JdbcBackend#Session) extends JdbcActionContext {
          val useSameThread = true
          override def session = s.asInstanceOf[Session]
          override def connection: Connection = s.conn
        }
      }

      def apply(db: JdbcProfile#Backend#Database, session: JdbcBackend#Session) =
        new WithSessionDatabaseDef(db, session)

    }
  }

}
