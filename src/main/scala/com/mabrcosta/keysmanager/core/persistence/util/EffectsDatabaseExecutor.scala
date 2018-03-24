package com.mabrcosta.keysmanager.core.persistence.util

import javax.inject.Inject
import org.atnos.eff.future._future
import org.atnos.eff.{Eff, FutureEffect}
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile


class EffectsDatabaseExecutor @Inject()(implicit val profile: JdbcProfile,
                                        implicit val db: JdbcProfile#Backend#Database) {

  implicit def apply[T](action: DBIO[T]): EffectsDatabaseActionExecutor[T] =
    new EffectsDatabaseActionExecutor[T](action)

  class EffectsDatabaseActionExecutor[T](private val action: DBIO[T])(implicit db: JdbcProfile#Backend#Database,
                                                                      profile: JdbcProfile) {
    def execute[R: _future]: Eff[R, T] = {
      import profile.api._
      FutureEffect.fromFuture(db.run(action.transactionally))
    }
  }
}
