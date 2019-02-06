package com.mabrcosta.keysmanager.users.business.api

import com.mabrcosta.keysmanager.core.business.api.BaseError

sealed trait UsersGroupsError extends BaseError

final case class UsersGroupNotFound(message: String) extends UsersGroupsError
