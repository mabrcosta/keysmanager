package com.mabrcosta.keysmanager.users.key.business

import java.util.UUID

import com.mabrcosta.keysmanager.core.business.StackInterpreter
import com.mabrcosta.keysmanager.core.business.api.Error
import com.mabrcosta.keysmanager.core.persistence.util._
import com.mabrcosta.keysmanager.users.business.api.Stack
import com.mabrcosta.keysmanager.users.business.api
import org.atnos.eff.concurrent.Scheduler
import org.atnos.eff.{Eff, ExecutorServices}
import org.mockito.Mockito
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Assertion, AsyncWordSpec}
import slick.dbio.{DBIO, DBIOAction}
import slick.jdbc.JdbcBackend

import scala.concurrent.Future

trait AbstractServiceSpec extends AsyncWordSpec with MockitoSugar {

  def mockDbInteraction[T](methodCall: DBIO[T], returnValue: T): Unit = {
    val execDbAction = Future.successful(returnValue)
    val dbAction = DBIOAction.from(execDbAction)

    Mockito.when(methodCall).thenReturn(dbAction)
  }

  def assertRight[T](effect: Eff[Stack, T], uidOwner: UUID, valueAssertion: T => Assertion): Future[Assertion] = {

    assertFutureSuccess[T](effect, uidOwner, {
      case Left(error)  => fail(s"Failed either assertion with left error: ${error.message}")
      case Right(value) => valueAssertion(value)
    })
  }

  def assertLeft[T](effect: Eff[Stack, T],
                    uidOwner: UUID,
                    errorAssertion: Error => Assertion): Future[Assertion] = {

    assertFutureSuccess[T](effect, uidOwner, {
      case Left(error)  => errorAssertion(error)
      case Right(value) => fail(s"Failed either assertion with right value: $value")
    })
  }

  def assertFutureSuccess[T](effect: Eff[Stack, T],
                             uidOwner: UUID,
                             valueAssertion: Either[Error, T] => Assertion): Future[Assertion] = {
    runStack(effect, uidOwner).map(valueAssertion).recover {
      case ex: Throwable => {
        ex.printStackTrace()
        fail(s"Failed future assertion with exception ${ex.getMessage}")
      }
    }
  }

  def runStack[T](effect: Eff[Stack, T], uidOwner: UUID): Future[Either[Error, T]] = {
    val db = JdbcBackend.Database.forURL("jdbc:h2:mem:test")
    val backend = new WithSessionJdbcBackend(db)
    val asyncDatabase = new JdbcProfileAsyncDatabase(db, backend)
    val scheduler: Scheduler = ExecutorServices.schedulerFromGlobalExecutionContext

    val interpreter = new StackInterpreter(asyncDatabase, executionContext, scheduler)

    interpreter.run(effect, uidOwner)
  }

}
