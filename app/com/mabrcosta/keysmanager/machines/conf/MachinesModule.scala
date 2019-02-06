package com.mabrcosta.keysmanager.machines.conf

import com.google.inject.{Provides, TypeLiteral}
import com.mabrcosta.keysmanager.core.persistence.util.EffDbExecutorId
import com.mabrcosta.keysmanager.machines.business.api.{MachinesGroupsService, MachinesService}
import com.mabrcosta.keysmanager.machines.business.func.{MachinesGroupsServiceImpl, MachinesServiceImpl}
import com.mabrcosta.keysmanager.machines.persistence.api.{MachinesDal, MachinesGroupMachinesDal, MachinesGroupsDal}
import com.mabrcosta.keysmanager.machines.persistence.func.{MachinesGroupMachinesRepository, MachinesGroupsRepository, MachinesRepository}
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaModule
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

class MachinesModule extends ScalaModule {

  override def configure(): Unit = {
    bind(new TypeLiteral[MachinesDal[DBIO]]() {}).to(classOf[MachinesRepository]).in(classOf[Singleton])
    bind(new TypeLiteral[MachinesGroupsDal[DBIO]]() {}).to(classOf[MachinesGroupsRepository]).in(classOf[Singleton])
    bind(new TypeLiteral[MachinesGroupMachinesDal[DBIO]]() {}).to(classOf[MachinesGroupMachinesRepository]).in(classOf[Singleton])
  }

  @Provides
  @Singleton
  def providesDBIOMachinesService(machinesDal: MachinesDal[DBIO],
                                  machinesGroupService: MachinesGroupsService[DBIO, DBIO],
                                  effectsDatabaseExecutor: EffDbExecutorId[DBIO],
                                  executionContext: ExecutionContext): MachinesService[DBIO, DBIO] = {

    new MachinesServiceImpl[DBIO, DBIO](
      machinesDal,
      machinesGroupService,
      effectsDatabaseExecutor,
      executionContext
    )
  }

  @Provides
  @Singleton
  def providesDBIOMachineGroupService(machinesGroups: MachinesGroupsDal[DBIO],
                                      machinesGroupMachinesDal: MachinesGroupMachinesDal[DBIO],
                                      effectsDatabaseExecutor: EffDbExecutorId[DBIO],
                                      executionContext: ExecutionContext): MachinesGroupsService[DBIO, DBIO] = {

    new MachinesGroupsServiceImpl[DBIO, DBIO](machinesGroups,
                                              machinesGroupMachinesDal,
                                              effectsDatabaseExecutor,
                                              executionContext)
  }

}
