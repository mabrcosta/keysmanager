package com.mabrcosta.keysmanager.users.key.business

import java.util.UUID

import com.mabrcosta.keysmanager.core.business.api.NotFound
import com.mabrcosta.keysmanager.core.persistence.util.EffDbExecutorId
import com.mabrcosta.keysmanager.users.business.api._
import com.mabrcosta.keysmanager.users.business.func.KeysServiceImpl
import com.mabrcosta.keysmanager.users.data.Key
import com.mabrcosta.keysmanager.users.persistence.api.KeysDal
import org.atnos.eff.MemberIn._
import org.mockito.invocation.InvocationOnMock
import org.mockito.{ArgumentMatcher, ArgumentMatchers, Mockito}
import slick.dbio.{DBIO, DBIOAction}

import scala.concurrent.{ExecutionContext, Future}

class KeysServiceSpec extends AbstractServiceSpec {

  val keysDal: KeysDal[DBIO] = mock[KeysDal[DBIO]]
  val effectsExecutor = new EffDbExecutorId[DBIO]
  val keyService = new KeysServiceImpl[DBIO, DBIO](keysDal, effectsExecutor, executionContext)

  val uidOwner: UUID = UUID.randomUUID()

  "Listing owner keys" when {
    "the owner have no keys" should {
      "return an empty sequence" in {
        mockDbInteraction(keysDal.findForOwner(uidOwner), Seq[Key]())
        assertRight[Seq[Key]](keyService.getForOwner[Stack],
                              uidOwner,
                              result => if (result.isEmpty) succeed else fail(result.toString()))
      }
    }
    "the owner have 2 keys" should {
      "return a sequence with 2 elements" in {
        val values =
          Seq(Key(value = "key1", uidOwnerSubject = uidOwner), Key(value = "key2", uidOwnerSubject = uidOwner))
        mockDbInteraction(keysDal.findForOwner(uidOwner), values)
        assertRight[Seq[Key]](keyService.getForOwner[Stack],
                              uidOwner,
                              result => if (result.size == 2 && result == values) succeed else fail(result.toString()))
      }
    }
  }

  "Adding keys" when {
    "adding a new key value" should {
      "return a persisted key with that value" in {
        val keyValue = "key_value"
        val execDbAction = Future.successful(Key(value = keyValue, uidOwnerSubject = uidOwner))
        val dbAction = DBIOAction.from(execDbAction)

        Mockito
          .when(keysDal.save(ArgumentMatchers.any(classOf[Key]))(ArgumentMatchers.any[ExecutionContext]()))
          .thenAnswer((invocation: InvocationOnMock) => {
            val key = invocation.getArgument[Key](0)
            if (key.value == keyValue) dbAction else fail()
          })

        assertRight[Key](
          keyService.add[Stack](keyValue),
          uidOwner,
          result => if (keyValueArgumentMatcher(keyValue).matches(result)) succeed else fail(result.toString))
      }
    }
  }

  "Deleting keys" when {
    "deleting a non-existent key for owner and uid" should {
      "return a NotFound error" in {
        val uidKey = UUID.randomUUID()
        val response: Option[Key] = None
        mockDbInteraction(keysDal.findForOwner(uidKey, uidOwner), response)
        val res = assertLeft[Boolean](keyService.delete[Stack](uidKey), uidOwner, {
          case NotFound(_) => succeed
          case res         => fail(res.toString)
        })
        Mockito.verify(keysDal).findForOwner(uidKey, uidOwner)

        res
      }
    }
    "deleting a valid key for owner and uid" should {
      "return true" in {
        val uidKey = UUID.randomUUID()
        val response = Key(id = Some(uidKey), value = "key_value", uidOwnerSubject = uidOwner)
        mockDbInteraction(keysDal.findForOwner(uidKey, uidOwner), Some(response))
        mockDbInteraction(keysDal.delete(response), response)
        assertRight[Boolean](keyService.delete[Stack](uidKey), uidOwner, result => if (result) succeed else fail)
      }
    }
  }

  def keyValueArgumentMatcher(keyValue: String): ArgumentMatcher[Key] =
    (argument: Key) => if (keyValue == argument.value) true else false

}
