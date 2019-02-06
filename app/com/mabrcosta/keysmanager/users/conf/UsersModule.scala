package com.mabrcosta.keysmanager.users.conf

import com.google.inject.{Provides, TypeLiteral}
import com.mabrcosta.keysmanager.core.persistence.util.EffDbExecutorId
import com.mabrcosta.keysmanager.users.business.api.{KeysService, UsersGroupsService, UsersService}
import com.mabrcosta.keysmanager.users.business.func.{KeysServiceImpl, UsersGroupsServiceImpl, UsersServiceImpl}
import com.mabrcosta.keysmanager.users.persistence.api.{KeysDal, UsersDal, UsersGroupsDal, UsersGroupsUsersDal}
import com.mabrcosta.keysmanager.users.persistence.func.{KeysRepository, UsersGroupsRepository, UsersGroupsUsersRepository, UsersRepository}
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaModule
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

class UsersModule extends ScalaModule {

  override def configure(): Unit = {
    bind(new TypeLiteral[KeysDal[DBIO]]() {}).to(classOf[KeysRepository]).in(classOf[Singleton])
    bind(new TypeLiteral[UsersDal[DBIO]]() {}).to(classOf[UsersRepository]).in(classOf[Singleton])
    bind(new TypeLiteral[UsersGroupsDal[DBIO]]() {}).to(classOf[UsersGroupsRepository]).in(classOf[Singleton])
    bind(new TypeLiteral[UsersGroupsUsersDal[DBIO]]() {}).to(classOf[UsersGroupsUsersRepository]).in(classOf[Singleton])
  }

  @Provides
  @Singleton
  def providesDBIOKeysService(keysDal: KeysDal[DBIO],
                              effectsDatabaseExecutor: EffDbExecutorId[DBIO],
                              executionContext: ExecutionContext): KeysService[DBIO, DBIO] = {

    new KeysServiceImpl[DBIO, DBIO](keysDal, effectsDatabaseExecutor, executionContext)
  }

  @Provides
  @Singleton
  def providesDBIOUsersService(usersDal: UsersDal[DBIO],
                               effectsDatabaseExecutor: EffDbExecutorId[DBIO],
                               executionContext: ExecutionContext): UsersService[DBIO, DBIO] = {

    new UsersServiceImpl[DBIO, DBIO](usersDal, effectsDatabaseExecutor, executionContext)
  }

  @Provides
  @Singleton
  def providesDBIOUserGroupsService(usersGroupsDal: UsersGroupsDal[DBIO],
                                    usersGroupsUsersDal: UsersGroupsUsersDal[DBIO],
                                    effectsDatabaseExecutor: EffDbExecutorId[DBIO],
                                    executionContext: ExecutionContext): UsersGroupsService[DBIO, DBIO] = {

    new UsersGroupsServiceImpl[DBIO, DBIO](usersGroupsDal,
                                           usersGroupsUsersDal,
                                           effectsDatabaseExecutor,
                                           executionContext)
  }

}
