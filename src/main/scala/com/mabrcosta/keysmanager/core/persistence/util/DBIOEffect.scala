package com.mabrcosta.keysmanager.core.persistence.util

import org.atnos.eff.Interpret.translate
import org.atnos.eff._
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcBackend

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

final class DBIOInterpretationOps[R, A](private val effect: Eff[R, A]) {

  def runDBIO[U](implicit m: Member.Aux[SlickDBIO, R, U],
                 future: FutureCreation._future[U],
                 futureDatabaseExecutor: FutureDatabaseExecutor,
                 session: JdbcBackend#Session): Eff[U, A] = {

//    futureDatabaseExecutor.withTransaction { implicit session =>
      translate(effect)(new Translate[SlickDBIO, U] {
        override def apply[X](kv: SlickDBIO[X]): Eff[U, X] = {
          val res = futureDatabaseExecutor(kv).execute
          FutureCreation.fromFuture(res)
        }
      })
//    }

  }
}
