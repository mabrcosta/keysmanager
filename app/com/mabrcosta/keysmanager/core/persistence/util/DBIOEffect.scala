package com.mabrcosta.keysmanager.core.persistence.util

import org.atnos.eff.Interpret.translate
import org.atnos.eff._
import slick.dbio.{DBIO => SlickDBIO}

object DBIOEffect extends DBIOCreation with DBIOInterpretation

trait DBIOTypes {
  type _dbio[R] = SlickDBIO |= R
  type _DBIO[R] = SlickDBIO <= R
  type _dbioIO[R] = SlickDBIO /= R
}

trait DBIOCreation extends DBIOTypes {
  def fromDBIO[R: _dbio, A](a: SlickDBIO[A]): Eff[R, A] = Eff.send[SlickDBIO, R, A](a)
}

trait DBIOInterpretation extends DBIOTypes {
  implicit def toDBIOInterpretationOps[R, A](effect: Eff[R, A]): DBIOInterpretationOps[R, A] =
    new DBIOInterpretationOps[R, A](effect)
}

final class DBIOInterpretationOps[R, A](private[this] val effect: Eff[R, A]) {

  def runDBIO[U](implicit m: Member.Aux[SlickDBIO, R, U],
                 future: FutureCreation._future[U],
                 sessionDatabase: WithProvidedSessionJdbcBackend#WithSessionDatabase): Eff[U, A] = {

    translate(effect)(new Translate[SlickDBIO, U] {
      override def apply[X](action: SlickDBIO[X]): Eff[U, X] = {
        FutureCreation.fromFuture(sessionDatabase.run(action))
      }
    })
  }
}
