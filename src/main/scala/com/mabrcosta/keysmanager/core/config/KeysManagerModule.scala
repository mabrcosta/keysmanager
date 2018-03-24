package com.mabrcosta.keysmanager.core.config

import java.net.URI

import com.google.inject.{AbstractModule, Provides, TypeLiteral}
import com.mabrcosta.keysmanager.core.data.{ServerAPIConfiguration, ServerConfiguration}
import com.mabrcosta.keysmanager.core.config.properties.ServerProperties
import com.mabrcosta.keysmanager.core.persistence.PersistenceSchema
import com.mabrcosta.keysmanager.core.persistence.util.DatabaseMigratorInfo
import com.mabrcosta.keysmanager.users.key.business.KeyServiceImpl
import com.mabrcosta.keysmanager.users.key.business.api.KeyService
import com.mabrcosta.keysmanager.users.key.persistence.{KeysDal, KeysRepository}
import com.typesafe.config.Config
import javax.inject.Singleton
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}
import slick.dbio.DBIO

import scala.collection.JavaConverters._

class KeysManagerModule extends ScalaModule {

  private lazy val migrationsBinder = ScalaMultibinder.newSetBinder[DatabaseMigratorInfo](binderAccess)
  protected[this] def addMigrationInfo: DatabaseMigratorInfo => Unit = migrationsBinder.addBinding.toInstance(_)

  override def configure(): Unit = {
    bind(new TypeLiteral[KeysDal[DBIO]]() {}).to(classOf[KeysRepository])
    bind[KeyService].to[KeyServiceImpl]

    addMigrationInfo(DatabaseMigratorInfo(PersistenceSchema.schema, Seq("com.mabrcosta.keysmanager.migration")))
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

}
