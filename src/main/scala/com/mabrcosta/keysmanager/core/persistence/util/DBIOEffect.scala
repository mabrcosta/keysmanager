package com.mabrcosta.keysmanager.core.persistence.util

import org.atnos.eff.Interpret.translate
import org.atnos.eff._
import slick.dbio.DBIO

object DBIOEffect extends DBIOCreation with DBIOInterpretation

trait DBIOTypes {
  type _dbio[R] = DBIO |= R
  type _DBIO[R] = DBIO <= R
  type _dbioIO[R] = DBIO /= R
}

trait DBIOCreation extends DBIOTypes {
  def fromDBIO[R: _dbio, A](a: DBIO[A]): Eff[R, A] = Eff.send[DBIO, R, A](a)
}

trait DBIOInterpretation extends DBIOTypes {
  implicit def toDBIOInterpretationOps[R, A](effect: Eff[R, A]): DBIOInterpretationOps[R, A] =
    new DBIOInterpretationOps[R, A](effect)
}

final class DBIOInterpretationOps[R, A](private val effect: Eff[R, A]) {

  def runDBIO[U](implicit m: Member.Aux[DBIO, R, U],
                 future: FutureCreation._future[U],
                 futureDatabaseExecutor: FutureDatabaseExecutor): Eff[U, A] = {

    translate(effect)(new Translate[DBIO, U] {
      override def apply[X](kv: DBIO[X]): Eff[U, X] = {
        FutureCreation.fromFuture(futureDatabaseExecutor(kv).execute)
      }
    })
  }
}
