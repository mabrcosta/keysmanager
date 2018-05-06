package com.mabrcosta.keysmanager.access.data

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.machines.data.{Machine, MachinesGroup}
import com.mabrcosta.keysmanager.users.data.{User, UsersGroup}

case class UserAccessCreationData(uid: UUID)
case class UsersGroupAccessCreationData(uid: UUID)

case class MachineAccessCreationData(uid: UUID)
case class MachinesGroupAccessCreationData(uid: UUID)

case class AccessProviderCreationData(startInstant: Instant,
                                      endInstant: Instant,
                                      userAccess: Either[UsersGroupAccessCreationData, UserAccessCreationData],
                                      machineAccess: Either[MachinesGroupAccessCreationData, MachineAccessCreationData])

case class AccessProviderData(uid: UUID,
                              startInstant: Instant,
                              endInstant: Instant,
                              userAccess: Either[UsersGroup, User],
                              machineAccess: Either[MachinesGroup, Machine])

object AccessProviderData {

  def apply(accessProvider: AccessProvider,
            userAccess: Either[UsersGroup, User],
            machineAccess: Either[MachinesGroup, Machine]): AccessProviderData =

    new AccessProviderData(accessProvider.id.get,
                           accessProvider.startInstant,
                           accessProvider.endInstant,
                           userAccess,
                           machineAccess)
}
