package com.mabrcosta.keysmanager.users.key.business

import com.mabrcosta.keysmanager.core.business.api.BaseError
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.core.persistence.util.DBIOEffect._
import com.mabrcosta.keysmanager.core.persistence.util.{EffDbExecutorId, JdbcProfileAsyncDatabase, WithSessionJdbcBackend}
import com.mabrcosta.keysmanager.users.business.api._
import com.mabrcosta.keysmanager.users.business.func.KeysServiceImpl
import com.mabrcosta.keysmanager.users.data.{Key, User}
import com.mabrcosta.keysmanager.users.key.business.KeysServiceSpec.KeysStack
import com.mabrcosta.keysmanager.users.persistence.api.KeysDal
import org.atnos.eff.MemberIn._
import org.atnos.eff.concurrent.Scheduler
import org.atnos.eff.syntax.all._
import org.atnos.eff.{Eff, FutureInterpretation, Fx, TimedFuture, _}
import org.mockito.invocation.InvocationOnMock
import org.mockito.{ArgumentMatcher, ArgumentMatchers, Mockito}
import slick.dbio.{DBIO, DBIOAction}
import slick.jdbc.JdbcBackend

import scala.com.mabrcosta.keysmanager.core.AbstractServiceSpec
import scala.concurrent.{ExecutionContext, Future}

object KeysServiceSpec {
  type KeysStack = Fx.fx4[OwnerReader[?], KeysErrorEither[?], DBIO, TimedFuture]
}

class KeysServiceSpec extends AbstractServiceSpec[KeysStack] {

  val keysDal: KeysDal[DBIO] = mock[KeysDal[DBIO]]
  val effectsExecutor = new EffDbExecutorId[DBIO]
  val keyService = new KeysServiceImpl[DBIO, DBIO](keysDal, effectsExecutor, executionContext)

  val ownerUserId: EntityId[User]= EntityId()

  "Listing owner keys" when {
    "the owner have no keys" should {
      "return an empty sequence" in {
        mockDbInteraction(keysDal.findForOwner(ownerUserId), Seq[Key]())
        assertRight[Seq[Key]](keyService.getForOwner[KeysStack],
                              ownerUserId,
                              result => if (result.isEmpty) succeed else fail(result.toString()))
      }
    }
    "the owner have 2 keys" should {
      "return a sequence with 2 elements" in {
        val values =
          Seq(Key(value = "key1", ownerUserId = ownerUserId), Key(value = "key2", ownerUserId = ownerUserId))
        mockDbInteraction(keysDal.findForOwner(ownerUserId), values)
        assertRight[Seq[Key]](keyService.getForOwner[KeysStack],
                              ownerUserId,
                              result => if (result.size == 2 && result == values) succeed else fail(result.toString()))
      }
    }
  }

  "Adding keys" when {
    "adding a new key value" should {
      "return a persisted key with that value" in {
        val keyValue = "key_value"
        val execDbAction = Future.successful(Key(value = keyValue, ownerUserId = ownerUserId))
        val dbAction = DBIOAction.from(execDbAction)

        Mockito
          .when(keysDal.save(ArgumentMatchers.any(classOf[Key]))(ArgumentMatchers.any[ExecutionContext]()))
          .thenAnswer((invocation: InvocationOnMock) => {
            val key = invocation.getArgument[Key](0)
            if (key.value == keyValue) dbAction else fail()
          })

        assertRight[Key](
          keyService.add[KeysStack](keyValue),
          ownerUserId,
          result => if (keyValueArgumentMatcher(keyValue).matches(result)) succeed else fail(result.toString))
      }
    }
  }

  "Deleting keys" when {
    "deleting a non-existent key for owner and uid" should {
      "return a KeyNotFound error" in {
        val keyId = EntityId[Key]()
        val response: Option[Key] = None
        mockDbInteraction(keysDal.findForOwner(keyId, ownerUserId), response)
        val res = assertLeft[Boolean](keyService.delete[KeysStack](keyId), ownerUserId, {
          case KeyNotFound(_) => succeed
          case res         => fail(res.toString)
        })
        Mockito.verify(keysDal).findForOwner(keyId, ownerUserId)

        res
      }
    }
    "deleting a valid key for owner and uid" should {
      "return true" in {
        val keyId = EntityId[Key]()
        val response = Key(id = Some(keyId), value = "key_value", ownerUserId = ownerUserId)
        mockDbInteraction(keysDal.findForOwner(keyId, ownerUserId), Some(response))
        mockDbInteraction(keysDal.delete(response), response)
        assertRight[Boolean](keyService.delete[KeysStack](keyId), ownerUserId, result => if (result) succeed else fail)
      }
    }
  }

  def keyValueArgumentMatcher(keyValue: String): ArgumentMatcher[Key] =
    (argument: Key) => if (keyValue == argument.value) true else false

  def runStack[T](effect: Eff[KeysStack, T], uidOwner: EntityId[User]): Future[Either[BaseError, T]] = {
    val db = JdbcBackend.Database.forURL("jdbc:h2:mem:test")
    val backend = new WithSessionJdbcBackend(db)
    val asyncDatabase = new JdbcProfileAsyncDatabase(db, backend)
    implicit val scheduler: Scheduler = ExecutorServices.schedulerFromGlobalExecutionContext

    import controllers.core.StackInterpreterIOValidatorOps._

    asyncDatabase.withTransaction { implicit sessionDatabase =>
      FutureInterpretation.runAsync(effect.runDBIO.runReader(uidOwner).runEither)
    }(_.validateRight(_.valid))
  }

}
