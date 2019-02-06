//package com.mabrcosta.keysmanager.users.rest
//
//import akka.http.scaladsl.model.StatusCodes._
//import akka.http.scaladsl.server.Directives._
//import akka.http.scaladsl.server.Route
//import com.mabrcosta.keysmanager.core.business.StackInterpreter
//import com.mabrcosta.keysmanager.core.rest.BaseHttpService
//import com.mabrcosta.keysmanager.users.business.api.{KeysService, Stack}
//import com.mabrcosta.keysmanager.users.data.{Key, KeyData}
//import com.typesafe.scalalogging.LazyLogging
//import javax.inject.Inject
//import slick.dbio.DBIO
//
//class KeysHttpService @Inject()(private val keyService: KeysService[DBIO, DBIO],
//                                private val stackInterpreter: StackInterpreter)
//    extends BaseHttpService(stackInterpreter) with KeysJsonSupport
//    with LazyLogging {
//
//  val routes: Route = pathPrefix("users" / JavaUUID / "keys") { uidOwner =>
//    get {
//      handleResponse[Seq[Key]](keyService.getForOwner[Stack], uidOwner, keys => complete(keys))
//    } ~ post {
//      entity(as[KeyData]) { key =>
//        handleResponse[Key](keyService.add[Stack](key.value), uidOwner, key => complete(key))
//      }
//    } ~ path(JavaUUID) { uidKey =>
//      delete {
//        handleResponse[Boolean](keyService.delete[Stack](uidKey),
//                                    uidOwner,
//                                    res => if (res) complete("") else complete(InternalServerError))
//      }
//    }
//  }
//
//}
