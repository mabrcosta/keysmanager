package com.mabrcosta.keysmanager.users.business.api

sealed trait Error {
  val message: String
}
final case class NotFound(message: String) extends Error
