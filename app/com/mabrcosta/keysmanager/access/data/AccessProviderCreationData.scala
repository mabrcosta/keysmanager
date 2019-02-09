package com.mabrcosta.keysmanager.access.data

import java.time.Instant

import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.machines.data.{Machine, MachinesGroup}
import com.mabrcosta.keysmanager.users.data.{User, UsersGroup}

case class UserAccessCreationData(userId: EntityId[User])
case class UsersGroupAccessCreationData(usersGroupId: EntityId[UsersGroup])

case class MachineAccessCreationData(machineId: EntityId[Machine])
case class MachinesGroupAccessCreationData(machinesGroupId: EntityId[MachinesGroup])

case class AccessProviderCreationData(startInstant: Instant,
                                      endInstant: Instant,
                                      userAccess: Either[UsersGroupAccessCreationData, UserAccessCreationData],
                                      machineAccess: Either[MachinesGroupAccessCreationData, MachineAccessCreationData])
