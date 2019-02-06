package com.mabrcosta.keysmanager.access.business.api
import com.mabrcosta.keysmanager.core.business.api.BaseError

sealed trait AccessError extends BaseError

final case class AccessProviderNotFound(message: String) extends AccessError
