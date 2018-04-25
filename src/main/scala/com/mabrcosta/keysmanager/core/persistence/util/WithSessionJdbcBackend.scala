package com.mabrcosta.keysmanager.core.persistence.util

import java.sql.Connection

import javax.inject.Inject
import org.reactivestreams.Subscriber
import slick.jdbc.{JdbcBackend, JdbcProfile}

trait WithSessionJdbcBackend extends JdbcBackend {

  type WithSessionDatabase = WithSessionDatabaseDef

  class WithSessionDatabaseDef(private val db: JdbcProfile#Backend#Database, val session: JdbcBackend#Session)
      extends DatabaseDef(db.source, db.executor) {

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

      val ctx = new JdbcStreamingActionContext(s, useSameThread, this, true) {
        override def session: Session = dbSession
        override def connection: Connection = dbSession.conn
      }
      ctx.pin
      ctx
    }
  }
}

class WithSessionBackend @Inject()(db: JdbcProfile#Backend#Database) extends WithSessionJdbcBackend {

  implicit def withSession(session: JdbcBackend#Session): WithSessionJdbcBackend#WithSessionDatabase = {
    new WithSessionDatabaseDef(db, session)
  }

}