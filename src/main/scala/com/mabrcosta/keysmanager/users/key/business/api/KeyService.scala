package com.mabrcosta.keysmanager.users.key.business.api

import java.util.UUID

import com.mabrcosta.keysmanager.users.key.data.Key
import org.atnos.eff.Eff
import org.atnos.eff.EitherEffect._throwableEither
import org.atnos.eff.future._future

trait KeyService {

  def getForOwner[R: _ownerReader : _future: _errorEither]: Eff[R, Seq[Key]]

  def addKey[R: _ownerReader : _future: _errorEither](keyValue: String): Eff[R, Key]

  def deleteKey[R: _ownerReader : _future: _errorEither](uid: UUID): Eff[R, Boolean]

}
