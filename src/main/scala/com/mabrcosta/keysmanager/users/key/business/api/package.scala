package com.mabrcosta.keysmanager.users.key.business

import org.atnos.eff.{Fx, TimedFuture, |=}

package object api {

  type ErrorEither[A] = Error Either A
  type _errorEither[R] = ErrorEither |= R

  type KeyStack = Fx.fx2[Either[Error, ?], TimedFuture]

}
