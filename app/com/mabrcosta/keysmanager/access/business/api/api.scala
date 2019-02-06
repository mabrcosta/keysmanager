package com.mabrcosta.keysmanager.access.business

import org.atnos.eff.|=

package object api {

  type AccessErrorEither[A] = AccessError Either A
  type _accessErrorEither[R] = AccessErrorEither |= R

}
