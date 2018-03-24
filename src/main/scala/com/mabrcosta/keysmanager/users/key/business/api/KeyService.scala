package com.mabrcosta.keysmanager.users.key.business.api

import java.util.UUID

import com.mabrcosta.keysmanager.users.key.data.Key
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect._throwableEither
import org.atnos.eff.future._future

trait KeyService {

  def getForOwner[R: _future: _errorEither](uidOwner: UUID): Eff[R, Seq[Key]]

  def addKey[R: _future: _errorEither](uidOwner: UUID, keyValue: String): Eff[R, Key]

  def deleteKey[R: _future: _errorEither](uid: UUID): Eff[R, Boolean]

}
