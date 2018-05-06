package com.mabrcosta.keysmanager.users.business

import java.util.UUID

import cats.data.Reader
import com.mabrcosta.keysmanager.core.business.api.Error
import org.atnos.eff.{Fx, TimedFuture, |=}
import slick.dbio.DBIO

package object api {

  type OwnerReader[A] = Reader[UUID, A]
  type _ownerReader[R] = OwnerReader |= R

  type ErrorEither[A] = Error Either A
  type _errorEither[R] = ErrorEither |= R

  type Stack = Fx.fx4[OwnerReader[?], ErrorEither[?], DBIO, TimedFuture]

}
