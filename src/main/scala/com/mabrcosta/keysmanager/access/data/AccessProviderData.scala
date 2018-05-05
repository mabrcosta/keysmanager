package com.mabrcosta.keysmanager.access.data

import java.time.Instant
import java.util.UUID

case class UserAccessData(uid: UUID)
case class UsersGroupAccessData(uid: UUID)

case class MachineAccessData(uid: UUID)
case class MachinesGroupAccessData(uid: UUID)

case class AccessProviderData(uid: Option[UUID],
                              startInstant: Instant,
                              endInstant: Instant,
                              userAccess: Either[UsersGroupAccessData, UserAccessData],
                              machineAccess: Either[MachinesGroupAccessData, MachineAccessData])
