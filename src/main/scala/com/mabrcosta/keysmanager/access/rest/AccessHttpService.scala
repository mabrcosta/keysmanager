package com.mabrcosta.keysmanager.access.rest

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.mabrcosta.keysmanager.access.business.api.AccessService
import com.mabrcosta.keysmanager.access.data.{AccessProviderCreationData, AccessProviderData}
import com.mabrcosta.keysmanager.core.business.StackInterpreter
import com.mabrcosta.keysmanager.core.rest.BaseHttpService
import com.mabrcosta.keysmanager.users.business.api.Stack
import javax.inject.Inject
import slick.dbio.DBIO

class AccessHttpService @Inject()(private val accessService: AccessService[DBIO, DBIO],
                                  private val stackInterpreter: StackInterpreter)
    extends BaseHttpService(stackInterpreter)
    with AccessJsonSupport {

  val routes: Route = pathPrefix("users" / JavaUUID / "keys") { uidOwner =>
//    get {
//      handleResponse[Seq[Key]](accessService.getForOwner[KeysStack], uidOwner, keys => complete(keys))
//    } ~
    post {
      entity(as[AccessProviderCreationData]) { data =>
        handleResponse[AccessProviderData](accessService.add[Stack](data), uidOwner, key => complete(key))
      }
    } ~ path(JavaUUID) { uidKey =>
      delete {
        handleResponse[Boolean](accessService.delete[Stack](uidKey),
                                uidOwner,
                                res => if (res) complete("") else complete(InternalServerError))
      }
    }
  }

}
