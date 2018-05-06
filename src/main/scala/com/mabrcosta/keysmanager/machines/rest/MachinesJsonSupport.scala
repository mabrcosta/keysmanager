package com.mabrcosta.keysmanager.machines.rest

import com.mabrcosta.keysmanager.core.rest.CoreSprayJsonSupport
import com.mabrcosta.keysmanager.machines.data.{Machine, MachinesGroup}
import spray.json.RootJsonFormat

trait MachinesJsonSupport extends CoreSprayJsonSupport {
  implicit val machineFormat: RootJsonFormat[Machine] = jsonFormat8(Machine)
  implicit val machinesGroupFormat: RootJsonFormat[MachinesGroup] = jsonFormat7(MachinesGroup)
}
