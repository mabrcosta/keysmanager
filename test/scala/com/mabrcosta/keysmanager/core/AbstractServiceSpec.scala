package scala.com.mabrcosta.keysmanager.core

import com.mabrcosta.keysmanager.core.business.api.BaseError
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.users.data.User
import org.atnos.eff.Eff
import org.mockito.Mockito
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Assertion, AsyncWordSpec}
import slick.dbio.{DBIO, DBIOAction}

import scala.concurrent.Future

trait AbstractServiceSpec[Stack] extends AsyncWordSpec with MockitoSugar {

  def mockDbInteraction[T](methodCall: DBIO[T], returnValue: T): Unit = {
    val execDbAction = Future.successful(returnValue)
    val dbAction = DBIOAction.from(execDbAction)

    Mockito.when(methodCall).thenReturn(dbAction)
  }

  def assertRight[T](effect: Eff[Stack, T],
                     ownerUserId: EntityId[User],
                     valueAssertion: T => Assertion): Future[Assertion] = {

    assertFutureSuccess[T](effect, ownerUserId, {
      case Left(error)  => fail(s"Failed either assertion with left error: ${error.message}")
      case Right(value) => valueAssertion(value)
    })
  }

  def assertLeft[T](effect: Eff[Stack, T],
                    ownerUserId: EntityId[User],
                    errorAssertion: BaseError => Assertion): Future[Assertion] = {

    assertFutureSuccess[T](effect, ownerUserId, {
      case Left(error)  => errorAssertion(error)
      case Right(value) => fail(s"Failed either assertion with right value: $value")
    })
  }

  def assertFutureSuccess[T](effect: Eff[Stack, T],
                             ownerUserId: EntityId[User],
                             valueAssertion: Either[BaseError, T] => Assertion): Future[Assertion] = {
    runStack(effect, ownerUserId).map(valueAssertion).recover {
      case ex: Throwable => {
        ex.printStackTrace()
        fail(s"Failed future assertion with exception ${ex.getMessage}")
      }
    }
  }

  def runStack[T](effect: Eff[Stack, T], ownerUserId: EntityId[User]): Future[Either[BaseError, T]]

}
