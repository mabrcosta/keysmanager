package com.mabrcosta.keysmanager.core.persistence.util

import org.atnos.eff.Eff
import org.atnos.eff.future._
import slick.dbio.{DBIO => SlickDBIO}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait DatabaseExecutor[MDBIO[_], Stack[_]] {
  def execute[T, R: Stack](action: MDBIO[T])(implicit ec: ExecutionContext): Eff[R, T]
  def execute[T, R: Stack](actions: Seq[MDBIO[T]])(implicit ec: ExecutionContext): Eff[R, Seq[T]]

  def executeWithResult[T, R: Stack](actions: Seq[MDBIO[T]], zero: T)(f: (T, T) => T)(
      implicit ec: ExecutionContext): Eff[R, T]

  def executeWithResult[R: Stack](actions: Seq[MDBIO[Int]])(implicit ec: ExecutionContext): Eff[R, Boolean]
  def executeWithResult[R: Stack](action: MDBIO[Int])(implicit ec: ExecutionContext): Eff[R, Boolean]
}

trait DatabaseExecutorDBIOToFuture extends DatabaseExecutor[SlickDBIO, _Future] {
  implicit protected val db: JdbcProfile#Backend#Database
  implicit protected val profile: JdbcProfile

  import profile.api._

  override def execute[T, R: _Future](action: DBIO[T])(implicit ec: ExecutionContext): Eff[R, T] = {
    fromFuture(db.run(action.transactionally))
  }

  /**
    * Executes database actions transactionally
    *
    * @param actions List of actions to execute
    * @tparam E
    */
  override def execute[T, R: _Future](actions: Seq[DBIO[T]])(implicit ec: ExecutionContext): Eff[R, Seq[T]] = {
    fromFuture(db.run(DBIO.sequence(actions).transactionally))
  }

  /**
    ** Executes database actions transactionally with IO result
    *
    * @param actions List of actions to execute
    * @param zero
    * @param f
    * @tparam T
    * @tparam E
    * @return
    */
  override def executeWithResult[T, R: _Future](actions: Seq[DBIO[T]], zero: T)(f: (T, T) => T)(
      implicit ec: ExecutionContext): Eff[R, T] = {
    fromFuture(db.run(DBIO.fold(actions, zero)(f).transactionally))
  }

  /**
    * Utility method for the special case of {@link executeWithResult} with type {@link DBIO[Int]}
    *
    * @param actions List of actions to execute
    * @param ec
    * @return
    */
  override def executeWithResult[R: _Future](actions: Seq[DBIO[Int]])(
      implicit ec: ExecutionContext): Eff[R, Boolean] = {
    executeWithResult(actions, 1)(integerFoldFunction).map(integerToBooleanMap)
  }

  /**
    * Utility method for the special case of {@link executeWithResult} with type {@link DBIO[Int]}
    *
    * @param actions List of actions to execute
    * @param ec
    * @return
    */
  override def executeWithResult[R: _Future](action: DBIO[Int])(implicit ec: ExecutionContext): Eff[R, Boolean] = {
    fromFuture(db.run(action.transactionally).map(integerToBooleanMap))
  }

  private def integerFoldFunction(p: Int, c: Int): Int = if (p == c) p else 0

  private def integerToBooleanMap(v: Int): Boolean = v match {
    case 1      => true
    case _: Int => false
  }
}
