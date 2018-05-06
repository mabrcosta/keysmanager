package com.mabrcosta.keysmanager

import java.net.URI

import com.google.inject.{Provides, TypeLiteral}
import com.mabrcosta.keysmanager.core.business.StackInterpreter
import com.mabrcosta.keysmanager.core.config.properties.ServerProperties
import com.mabrcosta.keysmanager.core.data.{ServerAPIConfiguration, ServerConfiguration}
import com.mabrcosta.keysmanager.core.persistence.PersistenceSchema
import com.mabrcosta.keysmanager.core.persistence.util.{DatabaseMigratorInfo, EffDbExecutorDBIOFuture, EffDbExecutorId, EffectsDatabaseExecutor}
import com.mabrcosta.keysmanager.users.business.KeysServiceImpl
import com.mabrcosta.keysmanager.users.business.api.KeysService
import com.mabrcosta.keysmanager.users.persistence.KeysRepository
import com.mabrcosta.keysmanager.users.persistence.api.KeysDal
import com.typesafe.config.Config
import javax.inject.Singleton
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}
import org.atnos.eff.concurrent.Scheduler
import org.atnos.eff.{ExecutorServices, TimedFuture}
import slick.dbio.DBIO

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

class KeysManagerModule extends ScalaModule {

  val migrationsResourcesPackage = "com.mabrcosta.keysmanager.migration"

  private lazy val migrationsBinder = ScalaMultibinder.newSetBinder[DatabaseMigratorInfo](binderAccess)
  private[this] def addMigrationInfo: DatabaseMigratorInfo => Unit = migrationsBinder.addBinding.toInstance(_)

  override def configure(): Unit = {
    bind(new TypeLiteral[KeysDal[DBIO]]() {}).to(classOf[KeysRepository]).in(classOf[Singleton])
    bind(new TypeLiteral[EffectsDatabaseExecutor[DBIO, TimedFuture]]() {})
      .to(classOf[EffDbExecutorDBIOFuture])
      .in(classOf[Singleton])

    bind[StackInterpreter].in(classOf[Singleton])

    addMigrationInfo(DatabaseMigratorInfo(PersistenceSchema.schema, Seq(migrationsResourcesPackage)))
  }

  @Provides
  @Singleton
  def providesDBIOFutureKeysService(keysDal: KeysDal[DBIO],
                                    effectsDatabaseExecutor: EffectsDatabaseExecutor[DBIO, TimedFuture],
                                    executionContext: ExecutionContext): KeysService[DBIO, TimedFuture] = {

    new KeysServiceImpl[DBIO, TimedFuture](keysDal, effectsDatabaseExecutor, executionContext)
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
  def provideServerConfiguration(config: Config): ServerConfiguration = {
    val serverConfig = config.getConfig(ServerProperties.CONFIG_KEY)

    val baseURL = URI.create(serverConfig.getString(ServerProperties.baseURL))
    val port = serverConfig.getInt(ServerProperties.port)
    val corsAllowedMethods = serverConfig.getStringList(ServerProperties.corsAllowedMethods)

    val apiBaseURL = URI.create(serverConfig.getString(ServerProperties.apiBaseURL))
    val api = ServerAPIConfiguration(apiBaseURL)

    ServerConfiguration(baseURL, port, corsAllowedMethods.asScala.toList, api)
  }

  @Provides
  @Singleton
  def provideEffectsScheduler: Scheduler = ExecutorServices.schedulerFromGlobalExecutionContext

}
