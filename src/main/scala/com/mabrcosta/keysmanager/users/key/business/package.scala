package com.mabrcosta.keysmanager.users.key

import org.atnos.eff.{Fx, TimedFuture}

package object business {

  type KeyStack = Fx.fx2[Either[Throwable, ?], TimedFuture]

}
