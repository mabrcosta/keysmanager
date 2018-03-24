package com.mabrcosta.keysmanager.core.config

import java.net.URI

import com.google.inject.{Provides, TypeLiteral}
import com.mabrcosta.keysmanager.core.config.properties.ServerProperties
import com.mabrcosta.keysmanager.core.data.{ServerAPIConfiguration, ServerConfiguration}
import com.mabrcosta.keysmanager.core.persistence.PersistenceSchema
import com.mabrcosta.keysmanager.core.persistence.util.{DatabaseMigratorInfo, EffDbExecutorDBIOFuture, EffectsDatabaseExecutor}
import com.mabrcosta.keysmanager.users.key.business.KeysServiceImpl
import com.mabrcosta.keysmanager.users.key.business.api.KeysService
import com.mabrcosta.keysmanager.users.key.persistence.KeysRepository
import com.mabrcosta.keysmanager.users.key.persistence.api.KeysDal
import com.typesafe.config.Config
import javax.inject.Singleton
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}
import org.atnos.eff.{ExecutorServices, TimedFuture}
import org.atnos.eff.concurrent.Scheduler
import slick.dbio.DBIO

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

class KeysManagerModule extends ScalaModule {

  val migrationsResourcesPackage = "com.mabrcosta.keysmanager.migration"

  private lazy val migrationsBinder = ScalaMultibinder.newSetBinder[DatabaseMigratorInfo](binderAccess)
  protected[this] def addMigrationInfo: DatabaseMigratorInfo => Unit = migrationsBinder.addBinding.toInstance(_)

  override def configure(): Unit = {
    bind(new TypeLiteral[KeysDal[DBIO]]() {}).to(classOf[KeysRepository]).in(classOf[Singleton])
    bind(new TypeLiteral[EffectsDatabaseExecutor[DBIO, TimedFuture]]() {})
      .to(classOf[EffDbExecutorDBIOFuture])
      .in(classOf[Singleton])

    addMigrationInfo(DatabaseMigratorInfo(PersistenceSchema.schema, Seq(migrationsResourcesPackage)))
  }

  @Provides
  @Singleton
  def providesDBIOFutureKeysService(
      keysDal: KeysDal[DBIO],
      effectsDatabaseExecutor: EffectsDatabaseExecutor[DBIO, TimedFuture],
      executionContext: ExecutionContext): KeysService[DBIO, TimedFuture] = {

    new KeysServiceImpl[DBIO, TimedFuture](keysDal, effectsDatabaseExecutor, executionContext)
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
  def provideExecutionContext: ExecutionContext = ExecutionContext.global

  @Provides
  @Singleton
  def provideEffectsScheduler: Scheduler = ExecutorServices.schedulerFromGlobalExecutionContext

}
