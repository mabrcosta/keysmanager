package com.mabrcosta.keysmanager.core.persistence

import org.atnos.eff.future._future
import org.atnos.eff.{Eff, FutureEffect}
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

object EffectsDatabaseExecutor {

  implicit def apply[T](action: DBIO[T])(implicit db: JdbcProfile#Backend#Database,
                                              profile: JdbcProfile): EffectsDatabaseExecutor[T] =
    new EffectsDatabaseExecutor[ T](action)

  class EffectsDatabaseExecutor[T](private val action: DBIO[T])(implicit db: JdbcProfile#Backend#Database,
                                                                     profile: JdbcProfile) {
    def execute[R : _future]: Eff[R, T] = {
      import profile.api._
      FutureEffect.fromFuture(db.run(action.transactionally))
    }
  }
}
