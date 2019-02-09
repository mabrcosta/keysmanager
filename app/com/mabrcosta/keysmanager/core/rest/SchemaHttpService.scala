//package com.mabrcosta.keysmanager.core.rest
//
//import akka.http.scaladsl.server.Directives._
//import akka.http.scaladsl.server.Route
//import com.mabrcosta.keysmanager.access.persistence.func.AccessProvidersRepository
//import com.mabrcosta.keysmanager.machines.persistence.{
//  MachinesAccessProvidersRepository,
//  MachinesGroupMachinesRepository,
//  MachinesGroupsRepository,
//  MachinesRepository
//}
//import com.mabrcosta.keysmanager.users.persistence.func._
//import com.typesafe.scalalogging.LazyLogging
//import javax.inject.Inject
//import slick.jdbc.JdbcProfile
//
//class SchemaHttpService @Inject()(private[this] val profile: JdbcProfile,
//                                  private[this] val users: UsersRepository,
//                                  private[this] val usersGroups: UsersGroupsRepository,
//                                  private[this] val usersGroupsUsers: UsersGroupsUsersRepository,
//                                  private[this] val usersAccessProviders: UsersAccessProvidersRepository,
//                                  private[this] val keys: KeysRepository,
//                                  private[this] val machines: MachinesRepository,
//                                  private[this] val machinesGroups: MachinesGroupsRepository,
//                                  private[this] val machinesGroupMachines: MachinesGroupMachinesRepository,
//                                  private[this] val machinesAccessProviders: MachinesAccessProvidersRepository,
//                                  private[this] val accessProviders: AccessProvidersRepository)
//    extends LazyLogging {
//
//  import profile.api._
//
//  val routes: Route = pathPrefix("schema") {
//    get {
//      complete(
//        (users.tableQuery.schema.create.statements ++
//          usersGroups.tableQuery.schema.create.statements ++
//          usersGroupsUsers.tableQuery.schema.create.statements ++
//          usersAccessProviders.tableQuery.schema.create.statements ++
//          keys.tableQuery.schema.create.statements ++
//          machines.tableQuery.schema.create.statements ++
//          machinesGroups.tableQuery.schema.create.statements ++
//          machinesGroupMachines.tableQuery.schema.create.statements ++
//          machinesAccessProviders.tableQuery.schema.create.statements ++
//          accessProviders.tableQuery.schema.create.statements).mkString(";\n"))
//    }
//  }
//
//}
