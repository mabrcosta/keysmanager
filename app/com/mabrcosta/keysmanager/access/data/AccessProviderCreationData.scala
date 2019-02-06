package com.mabrcosta.keysmanager.access.data

import java.time.Instant
import java.util.UUID

case class UserAccessCreationData(uid: UUID)
case class UsersGroupAccessCreationData(uid: UUID)

case class MachineAccessCreationData(uid: UUID)
case class MachinesGroupAccessCreationData(uid: UUID)

case class AccessProviderCreationData(startInstant: Instant,
                                      endInstant: Instant,
                                      userAccess: Either[UsersGroupAccessCreationData, UserAccessCreationData],
                                      machineAccess: Either[MachinesGroupAccessCreationData, MachineAccessCreationData])
