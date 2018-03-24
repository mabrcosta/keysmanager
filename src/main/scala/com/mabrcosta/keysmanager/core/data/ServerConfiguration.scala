package com.mabrcosta.keysmanager.core.data

import java.net.URI

case class ServerConfiguration(baseURI: URI, port: Int, corsAllowedMethods: List[String], api: ServerAPIConfiguration)

case class ServerAPIConfiguration(baseURI: URI)
