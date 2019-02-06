package com.mabrcosta.keysmanager.users.business.api

import com.mabrcosta.keysmanager.core.business.api.BaseError

sealed trait UsersError extends BaseError

final case class UserNotFound(message: String) extends UsersError
