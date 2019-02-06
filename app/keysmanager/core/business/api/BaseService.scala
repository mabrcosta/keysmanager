package com.mabrcosta.keysmanager.core.business.api

import org.atnos.eff.|=

trait BaseService [TDBIO[_], TDBOut[_]] {

  type _tDBOut[R] = TDBOut |= R

}
