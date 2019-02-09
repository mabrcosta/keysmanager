package com.mabrcosta.keysmanager.core.config

import java.net.URI

import com.google.inject.{AbstractModule, Provides}
import com.mabrcosta.keysmanager.core.config.properties.ServerProperties
import com.mabrcosta.keysmanager.core.data.{ServerAPIConfiguration, ServerConfiguration}
import com.typesafe.config.Config
import controllers.machines.interpreters.MachinesStackInterpreter
import javax.inject.Singleton
import org.atnos.eff.ExecutorServices
import org.atnos.eff.concurrent.Scheduler

import scala.collection.JavaConverters._

class CoreModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[MachinesStackInterpreter]).in(classOf[Singleton])
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
