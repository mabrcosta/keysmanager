package com.mabrcosta.keysmanager.machines.key

import scala.concurrent.Future

package object business {

  type KeyFE[TOut] = Future[Either[Exception, TOut]]

}
