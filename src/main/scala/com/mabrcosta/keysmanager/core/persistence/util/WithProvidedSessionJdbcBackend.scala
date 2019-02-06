package com.mabrcosta.keysmanager.core.persistence.util

import java.sql.Connection

import javax.inject.Inject
import org.reactivestreams.Subscriber
import slick.JdbcProfileAsyncSession
import slick.jdbc.{JdbcBackend, JdbcProfile}

import scala.concurrent.{ExecutionContext, Future}

trait WithProvidedSessionJdbcBackend extends JdbcBackend {

  type WithSessionDatabase = WithSessionDatabaseDef

  class WithSessionDatabaseDef(private val db: JdbcProfile#Backend#Database, val session: JdbcBackend#Session)
      extends DatabaseDef(db.source, db.executor)
      with JdbcProfileAsyncSession {

    private val dbSession = session.asInstanceOf[Session]

    override def createSession(): Session = dbSession

    override protected[this] def createDatabaseActionContext[T](_useSameThread: Boolean): Context = {
      val ctx = new JdbcActionContext {
        val useSameThread: Boolean = _useSameThread
        override def session: Session = dbSession
        override def connection: Connection = dbSession.conn
      }
      ctx.pin
      ctx
    }

    override protected[this] def createStreamingDatabaseActionContext[T](s: Subscriber[_ >: T],
                                                                         useSameThread: Boolean): StreamingContext = {

      val ctx = new JdbcStreamingActionContext(s, true, this, true) {
        override def session: Session = dbSession
        override def connection: Connection = dbSession.conn
      }
      ctx.pin
      ctx
    }

    def withTransaction[T](f: => Future[T])(isSuccess: T => Boolean)(implicit ec: ExecutionContext): Future[T] = {
      session.withTransaction(f)(isSuccess)
    }
  }
}

class WithSessionJdbcBackend @Inject()(db: JdbcProfile#Backend#Database) extends WithProvidedSessionJdbcBackend {

  implicit def withSession(session: JdbcBackend#Session): WithProvidedSessionJdbcBackend#WithSessionDatabase = {
    new WithSessionDatabaseDef(db, session)
  }

}
