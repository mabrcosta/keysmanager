package com.mabrcosta.keysmanager.core.config

import java.net.URI

import com.google.inject.Provides
import com.mabrcosta.keysmanager.core.config.properties.ServerProperties
import com.mabrcosta.keysmanager.core.data.{ServerAPIConfiguration, ServerConfiguration}
import com.mabrcosta.keysmanager.core.persistence.PersistenceSchema
import com.mabrcosta.keysmanager.core.persistence.util.DatabaseMigratorInfo
import com.typesafe.config.Config
import controllers.machines.interpreters.MachinesStackInterpreter
import javax.inject.Singleton
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}
import org.atnos.eff.ExecutorServices
import org.atnos.eff.concurrent.Scheduler

import scala.collection.JavaConverters._

class CoreModule extends ScalaModule {

  val migrationsResourcesPackage = "com.mabrcosta.keysmanager.migration"

  private[this] lazy val migrationsBinder = ScalaMultibinder.newSetBinder[DatabaseMigratorInfo](binderAccess)
  private[this] def addMigrationInfo: DatabaseMigratorInfo => Unit = migrationsBinder.addBinding.toInstance(_)

  override def configure(): Unit = {
    bind[MachinesStackInterpreter].in(classOf[Singleton])

    addMigrationInfo(DatabaseMigratorInfo(PersistenceSchema.schema, Seq(migrationsResourcesPackage)))
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
