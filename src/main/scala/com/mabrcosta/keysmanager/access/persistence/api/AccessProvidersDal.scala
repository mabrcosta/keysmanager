package com.mabrcosta.keysmanager.access.persistence.api

import java.time.Instant
import java.util.UUID

import com.mabrcosta.keysmanager.access.data.AccessProvider
import com.mabrcosta.keysmanager.core.persistence.DatabaseDal

trait AccessProvidersDal[TIO[_]] extends DatabaseDal[AccessProvider, UUID, TIO] {

  def findForMachinesProviders(uidMachinesProviders: Seq[UUID], at: Instant): TIO[Seq[AccessProvider]]

}
