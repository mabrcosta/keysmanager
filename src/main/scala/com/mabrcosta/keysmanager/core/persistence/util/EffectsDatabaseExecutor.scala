package com.mabrcosta.keysmanager.core.persistence.util

import org.atnos.eff.{Eff, |=}

trait EffectsDatabaseExecutor[TIOIn[_], TOut[_]] {
  implicit def apply[T](action: TIOIn[T]): EffectsDatabaseActionExecutor[TIOIn, TOut, T]
}

trait EffectsDatabaseActionExecutor[TIOIn[_], TOut[_], T] {
  type _tOut[R] = TOut |= R

  def execute[R: _tOut]: Eff[R, T]
}
