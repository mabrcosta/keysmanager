package com.mabrcosta.keysmanager.machines.business.api

import com.mabrcosta.keysmanager.core.business.api.BaseError

sealed trait MachinesError extends BaseError

final case class MachineNotFound(message: String) extends MachinesError
