package com.mabrcosta.keysmanager.users.core.persistence

import java.sql.SQLException
import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.util.{
  JdbcProfileAsyncDatabase,
  WithProvidedSessionJdbcBackend,
  WithSessionJdbcBackend
}
import com.mabrcosta.keysmanager.users.data.Key
import com.mabrcosta.keysmanager.users.persistence.KeysRepository
import org.scalatest.{AsyncWordSpec, FutureOutcome}
import slick.jdbc
import slick.jdbc.{H2Profile, JdbcBackend}

import scala.concurrent.Future

class JdbcProfileAsyncDatabaseSpec extends AsyncWordSpec {

  val db: jdbc.JdbcBackend.DatabaseDef =
    JdbcBackend.Database.forURL("jdbc:h2:mem:test_mem;MODE=PostgreSQL;DB_CLOSE_DELAY=-1")

  val backend = new WithSessionJdbcBackend(db)
  val asyncDatabase = new JdbcProfileAsyncDatabase(db, backend)

  val profile = H2Profile
  val repository = new KeysRepository(profile)

  val uidKey: UUID = UUID.randomUUID()
  val keyValue = "key value"
  val key = Key(id = Some(uidKey), value = keyValue, uidOwnerSubject = UUID.randomUUID())

  import profile.api._

  override def withFixture(test: NoArgAsyncTest): FutureOutcome = {
    complete {
      new FutureOutcome(db.run(repository.tableQuery.schema.create).flatMap(_ => super.withFixture(test).toFuture))
    } lastly {
      db.run(repository.tableQuery.schema.drop)
    }
  }

  "having a database and a repository" when {

    "doing an insert on a success transaction" should {
      "return the key when searched" in {
        withTransaction(asyncDatabase, s => s.run(repository.save(key))).flatMap(
          _ =>
            db.run(repository.find(uidKey))
              .map({
                case Some(k) => assert(k.equals(key))
                case None    => fail()
              }))
      }
    }

    "doing an insert on a failed single action transaction" should {
      "return absent when searched" in {
        withFailedTransaction(asyncDatabase, s => s.run(repository.save(key))).flatMap(
          _ =>
            db.run(repository.find(uidKey))
              .map({
                case Some(_) => fail()
                case None    => succeed
              }))
      }
    }

    "doing an insert on a failed multiple actions transaction" should {
      "return absent when searched" in {
        val key2 = Key(id = Some(UUID.randomUUID()), value = keyValue, uidOwnerSubject = UUID.randomUUID())

        withTransaction(asyncDatabase, s => {
          s.run(repository.save(key)).flatMap(_ => s.run(repository.save(key2)))
        }).recover({
            case _: SQLException => {
              "Failed Transaction"
            }
          })
          .flatMap(_ =>
            db.run(repository.find(Seq(uidKey, key2.id.get)))
              .map(res => {
                if (res.nonEmpty) fail() else succeed
              }))
      }
    }

    "doing an insert with a nested outer transaction fail" should {
      "return absent when searched" in {
        val key2 = Key(id = Some(UUID.randomUUID()), value = "key value 2", uidOwnerSubject = UUID.randomUUID())

        withFailedTransaction(asyncDatabase, s => {
          s.run(repository.save(key)).flatMap(_ => s.withTransaction(s.run(repository.save(key2)))( _ => true))
        }).flatMap(_ =>
          db.run(repository.find(Seq(uidKey, key2.id.get)))
            .map(res => {
              if (res.nonEmpty) fail() else succeed
            }))
      }
    }

    "doing an insert with a nested inner transaction fail" should {
      "return just the outer saved key" in {
        val key2 = Key(id = Some(UUID.randomUUID()), value = "key value 2", uidOwnerSubject = UUID.randomUUID())

        withTransaction(asyncDatabase, s => {
          s.run(repository.save(key)).flatMap(_ => s.withTransaction(s.run(repository.save(key2)))( _ => false))
        }).flatMap(_ =>
          db.run(repository.find(Seq(uidKey, key2.id.get)))
            .map(res => {
              if (res.equals(Seq(key))) succeed else fail()
            }))
      }
    }

  }

  private def withTransaction[T](asyncDatabase: JdbcProfileAsyncDatabase,
                                 f: (WithProvidedSessionJdbcBackend#WithSessionDatabase) => Future[T]) = {
    asyncDatabase.withTransaction { session =>
      f(session)
    } { _ =>
      true
    }
  }

  private def withFailedTransaction[T](asyncDatabase: JdbcProfileAsyncDatabase,
                                       f: (WithProvidedSessionJdbcBackend#WithSessionDatabase) => Future[T]) = {
    withTransaction(asyncDatabase, s => {
      f(s).flatMap(_ => Future.failed(new TestException()))
    }).recover({
      case _: TestException => "Failed Transaction"
    })
  }

  class TestException extends Exception

}
