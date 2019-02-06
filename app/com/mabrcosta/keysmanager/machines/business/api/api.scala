package com.mabrcosta.keysmanager.machines.business

import org.atnos.eff.|=

package object api {

  type MachinesErrorEither[A] = MachinesError Either A
  type _machinesErrorEither[R] = MachinesErrorEither |= R

  type MachinesGroupsErrorEither[A] = MachinesGroupsError Either A
  type _machinesGroupsErrorEither[R] = MachinesGroupsErrorEither |= R

}
