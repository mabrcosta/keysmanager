package com.mabrcosta.keysmanager.core.persistence.util

import javax.inject.Inject
import slick.JdbcProfileAsyncSession
import slick.jdbc.JdbcBackend

import scala.concurrent.{ExecutionContext, Future}

class JdbcProfileAsyncDatabase @Inject()(db: JdbcBackend#Database,
                                         backend: WithSessionBackend) extends JdbcProfileAsyncSession {
  implicit val executionContext: ExecutionContext = db.executor.executionContext

  def withSimpleSession[T](f: (JdbcBackend#Session) => Future[T]): Future[T] = {
    val session = db.createSession()
    val res = f(session)
    res.onComplete(_ => session.close())
    res
  }

  def withSimpleTransaction[T](f: (JdbcBackend#Session) => Future[T]): Future[T] =
    withSimpleSession { s =>
      s.withTransaction(f(s))
    }

  def withSession[T](f: (WithSessionJdbcBackend#WithSessionDatabase) => Future[T]): Future[T] = {
    withSimpleSession { session =>
      f(backend.withSession(session))
    }
  }

  def withTransaction[T](f: (WithSessionJdbcBackend#WithSessionDatabase) => Future[T]): Future[T] = {
    withSession { db =>
      db.session.withTransaction(f(db))
    }
  }
}
