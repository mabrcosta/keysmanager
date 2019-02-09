//package com.mabrcosta.keysmanager.machines.rest
//
//import akka.http.scaladsl.server.Directives.{pathPrefix, _}
//import akka.http.scaladsl.server.Route
//import com.mabrcosta.keysmanager.core.business.StackInterpreter
//import com.mabrcosta.keysmanager.core.rest.BaseHttpService
//import com.mabrcosta.keysmanager.machines.business.api.MachinesService
//import com.mabrcosta.keysmanager.users.business.api.Stack
//import com.mabrcosta.keysmanager.users.data.Key
//import com.mabrcosta.keysmanager.users.rest.KeysJsonSupport
//import javax.inject.Inject
//import slick.dbio.DBIO
//
//class MachinesHttpService @Inject()(private[this] val machinesService: MachinesService[DBIO, DBIO],
//                                    private[this] val stackInterpreter: StackInterpreter)
//    extends BaseHttpService(stackInterpreter)
//    with KeysJsonSupport {
//
//  val routes: Route = pathPrefix("machines" / Segment / "authorized_keys") { hostname =>
//    get {
//      handleResponse[Seq[Key]](machinesService.getAuthorizedKeys[Stack](hostname),
//                               null,
//                               keys => complete(keys.map(_.value).mkString("\n")))
//    }
//  }
//
//}
