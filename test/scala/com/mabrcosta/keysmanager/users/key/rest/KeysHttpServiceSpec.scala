//package com.mabrcosta.keysmanager.users.key.rest
//
//import java.util.UUID
//
//import akka.http.scaladsl.model.StatusCodes
//import akka.http.scaladsl.testkit.ScalatestRouteTest
//import com.mabrcosta.keysmanager.core.business.StackInterpreter
//import com.mabrcosta.keysmanager.core.business.api.{Error, NotFound}
//import com.mabrcosta.keysmanager.users.business.api.{KeysService, Stack}
//import com.mabrcosta.keysmanager.users.data.{Key, KeyData}
//import com.mabrcosta.keysmanager.users.rest.KeysHttpService
//import org.atnos.eff.{Eff, EitherCreation}
//import org.mockito.{ArgumentMatchers, Mockito}
//import org.scalatest.mockito.MockitoSugar
//import org.scalatest.{AsyncWordSpec, Matchers}
//import slick.dbio.DBIO
//
//import scala.concurrent.Future
//
//class KeysHttpServiceSpec extends AsyncWordSpec with ScalatestRouteTest with Matchers with MockitoSugar {
//
//  import com.mabrcosta.keysmanager.users.rest.KeysJsonSupport._
//
//  val keyService: KeysService[DBIO, DBIO] = mock[KeysService[DBIO, DBIO]]
//  val interpreter: StackInterpreter = mock[StackInterpreter]
//  val keysHttpService = new KeysHttpService(keyService, interpreter)
//
//  val uidOwnerStr = "0d3fcc37-d330-4c7c-8d82-128235617d7d"
//  val uidOwner: UUID = UUID.fromString(uidOwnerStr)
//
//  val basePath = s"/users/$uidOwnerStr/keys"
//
//  "Listing owner keys" when {
//
//    "the owner have no keys" should {
//      "return an empty sequence" in {
//        val values = Seq()
//        val effect = Eff.pure[Stack, Seq[Key]](values)
//
//        Mockito.when(keyService.getForOwner[Stack]).thenReturn(effect)
//        mockEffectInterpreter(effect, values)
//
//        Get(basePath) ~> keysHttpService.routes ~> check {
//          status shouldEqual StatusCodes.OK
//          entityAs[Seq[Key]] shouldEqual Seq()
//        }
//      }
//    }
//    "the owner have 2 keys" should {
//      "return a sequence with 2 elements" in {
//        val values =
//          Seq(Key(value = "key1", uidOwnerSubject = uidOwner), Key(value = "key2", uidOwnerSubject = uidOwner))
//        val effect = Eff.pure[Stack, Seq[Key]](values)
//
//        Mockito.when(keyService.getForOwner[Stack]).thenReturn(effect)
//        mockEffectInterpreter(effect, values)
//
//        Get(basePath) ~> keysHttpService.routes ~> check {
//          status shouldEqual StatusCodes.OK
//          entityAs[Seq[Key]] shouldEqual values
//        }
//      }
//    }
//  }
//
//  "Adding keys" when {
//    "adding a new key value" should {
//      "return a persisted key with that value" in {
//        val keyValue = "key_value"
//        val key = Key(value = keyValue, uidOwnerSubject = uidOwner)
//        val effect = Eff.pure[Stack, Key](key)
//
//        Mockito
//          .when(keyService.add[Stack](ArgumentMatchers.eq(keyValue))(ArgumentMatchers.any(),
//                                                                                ArgumentMatchers.any()))
//          .thenReturn(Eff.pure[Stack, Key](key))
//
//        mockEffectInterpreter(effect, key)
//
//        Post(basePath, KeyData(keyValue)) ~> keysHttpService.routes ~> check {
//          status shouldEqual StatusCodes.OK
//          entityAs[Key].value shouldEqual keyValue
//        }
//      }
//    }
//  }
//
//  "Deleting keys" when {
//    "deleting a non-existent key for owner and uid" should {
//      "return a NotFound error" in {
//        val uidKey = UUID.randomUUID()
//        val error = NotFound(s"Unable to find key with uid $uidKey")
//        val effect = EitherCreation.left[Stack, Error, Boolean](error)
//
//        Mockito
//          .when(
//            keyService.delete[Stack](ArgumentMatchers.eq(uidKey))(ArgumentMatchers.any(),
//                                                                             ArgumentMatchers.any(),
//                                                                             ArgumentMatchers.any()))
//          .thenReturn(effect)
//
//        mockEffectInterpreter(effect, error)
//
//        Delete(basePath + s"/$uidKey") ~> keysHttpService.routes ~> check {
//          status shouldEqual StatusCodes.NotFound
//          entityAs[String] shouldEqual error.message
//        }
//      }
//    }
//    "deleting a valid key for owner and uid" should {
//      "return true" in {
//        val uidKey = UUID.randomUUID()
//        val result = true
//        val effect = Eff.pure[Stack, Boolean](result)
//
//        Mockito
//          .when(
//            keyService.delete[Stack](ArgumentMatchers.eq(uidKey))(ArgumentMatchers.any(),
//                                                                             ArgumentMatchers.any(),
//                                                                             ArgumentMatchers.any()))
//          .thenReturn(effect)
//
//        mockEffectInterpreter(effect, result)
//
//        Delete(basePath + s"/$uidKey") ~> keysHttpService.routes ~> check {
//          status shouldEqual StatusCodes.OK
//        }
//      }
//    }
//  }
//
//  private def mockEffectInterpreter[T](effect: Eff[Stack, T], result: T): Unit = {
//    mockEffectInterpreter(effect, Right(result))
//  }
//
//  private def mockEffectInterpreter[T](effect: Eff[Stack, T], error: Error): Unit = {
//    mockEffectInterpreter(effect, Left(error))
//  }
//
//  private def mockEffectInterpreter[T](effect: Eff[Stack, T], result: Either[Error, T]): Unit = {
//    Mockito
//      .when(interpreter.run(ArgumentMatchers.eq(effect), ArgumentMatchers.eq(uidOwner)))
//      .thenReturn(Future.successful(result))
//  }
//
//}
