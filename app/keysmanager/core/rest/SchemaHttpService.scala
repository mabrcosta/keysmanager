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
//class SchemaHttpService @Inject()(private val profile: JdbcProfile,
//                                  private val users: UsersRepository,
//                                  private val usersGroups: UsersGroupsRepository,
//                                  private val usersGroupsUsers: UsersGroupsUsersRepository,
//                                  private val usersAccessProviders: UsersAccessProvidersRepository,
//                                  private val keys: KeysRepository,
//                                  private val machines: MachinesRepository,
//                                  private val machinesGroups: MachinesGroupsRepository,
//                                  private val machinesGroupMachines: MachinesGroupMachinesRepository,
//                                  private val machinesAccessProviders: MachinesAccessProvidersRepository,
//                                  private val accessProviders: AccessProvidersRepository)
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
