package com.mabrcosta.keysmanager.machines.business.api

import com.mabrcosta.keysmanager.core.business.api.BaseError

sealed trait MachinesGroupsError extends BaseError

final case class MachinesGroupNotFound(message: String) extends MachinesGroupsError
