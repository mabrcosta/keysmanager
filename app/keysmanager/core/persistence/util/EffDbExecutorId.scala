package com.mabrcosta.keysmanager.core.persistence.util

import org.atnos.eff.Eff


final class EffDbExecutorId[M[_]] extends EffectsDatabaseExecutor[M, M] {

  implicit def apply[T](action: M[T]): EffectsDatabaseActionExecutor[M, M, T] =
    new EffDbActionExecutorId[M, T](action)

  class EffDbActionExecutorId[M[_], T](private[this] val action: M[T])
      extends EffectsDatabaseActionExecutor[M, M, T] {

    def execute[R: _tOut]: Eff[R, T] = Eff.send(action)
  }

}
