package com.mabrcosta.keysmanager.access.conf

import com.google.inject.{AbstractModule, Provides, TypeLiteral}
import com.mabrcosta.keysmanager.access.business.api.AccessService
import com.mabrcosta.keysmanager.access.business.func.AccessServiceImpl
import com.mabrcosta.keysmanager.access.persistence.api.AccessProvidersDal
import com.mabrcosta.keysmanager.access.persistence.func.AccessProvidersRepository
import com.mabrcosta.keysmanager.core.persistence.util.EffDbExecutorId
import com.mabrcosta.keysmanager.machines.business.api.{MachinesGroupsService, MachinesService}
import com.mabrcosta.keysmanager.users.business.api.{KeysService, UsersGroupsService, UsersService}
import javax.inject.Singleton
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

class AccessModule extends AbstractModule {

  override def configure(): Unit = {
    bind(new TypeLiteral[AccessProvidersDal[DBIO]]() {}).to(classOf[AccessProvidersRepository]).in(classOf[Singleton])
  }

  @Provides
  @Singleton
  def providesDBIOAccessService(accessProvidersDal: AccessProvidersDal[DBIO],
                                machinesService: MachinesService[DBIO, DBIO],
                                machinesGroupService: MachinesGroupsService[DBIO, DBIO],
                                usersService: UsersService[DBIO, DBIO],
                                usersGroupService: UsersGroupsService[DBIO, DBIO],
                                keysService: KeysService[DBIO, DBIO],
                                effectsDatabaseExecutor: EffDbExecutorId[DBIO],
                                executionContext: ExecutionContext): AccessService[DBIO, DBIO] = {

    new AccessServiceImpl[DBIO, DBIO](accessProvidersDal,
                                      machinesService,
                                      machinesGroupService,
                                      usersService,
                                      usersGroupService,
                                      keysService,
                                      effectsDatabaseExecutor,
                                      executionContext)
  }

}
