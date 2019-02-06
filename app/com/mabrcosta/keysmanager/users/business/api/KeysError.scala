package com.mabrcosta.keysmanager.users.business.api

import com.mabrcosta.keysmanager.core.business.api.BaseError

sealed trait KeysError extends BaseError

final case class KeyNotFound(message: String) extends KeysError
