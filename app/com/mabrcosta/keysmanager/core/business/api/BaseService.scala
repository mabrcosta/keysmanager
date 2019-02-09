package com.mabrcosta.keysmanager.core.business.api

import org.atnos.eff.|=

trait BaseService [TIOIn[_], TIOOut[_]] {

  type _TIOOut[R] = TIOOut |= R

}
