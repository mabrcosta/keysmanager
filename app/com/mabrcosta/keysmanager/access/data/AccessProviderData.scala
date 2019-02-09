package com.mabrcosta.keysmanager.access.data

import java.time.Instant

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.machines.data.{Machine, MachinesGroup}
import com.mabrcosta.keysmanager.users.data.{User, UsersGroup}

case class AccessProviderData(id: EntityId[AccessProvider],
                              startInstant: Instant,
                              endInstant: Instant,
                              userAccess: Either[UsersGroup, User],
                              machineAccess: Either[MachinesGroup, Machine])

object AccessProviderData {

  def apply(accessProvider: AccessProvider,
            userAccess: Either[UsersGroup, User],
            machineAccess: Either[MachinesGroup, Machine]): AccessProviderData = {

    new AccessProviderData(accessProvider.id.get,
                           accessProvider.startInstant,
                           accessProvider.endInstant,
                           userAccess,
                           machineAccess)
  }
}
