package controllers.machines.interpreters

import com.mabrcosta.keysmanager.core.persistence.util.DBIOEffect._
import com.mabrcosta.keysmanager.core.persistence.util.JdbcProfileAsyncDatabase
import com.mabrcosta.keysmanager.machines.business.api.{MachinesError, MachinesErrorEither}
import controllers.machines.interpreters.MachinesStackInterpreter.MachinesStack
import javax.inject.Inject
import org.atnos.eff.concurrent.Scheduler
import org.atnos.eff.syntax.all._
import org.atnos.eff.{Eff, FutureInterpretation, Fx, TimedFuture}
import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}

class MachinesStackInterpreter @Inject()(private val database: JdbcProfileAsyncDatabase,
                                         implicit val executionContext: ExecutionContext,
                                         implicit val scheduler: Scheduler) {

  import controllers.core.StackInterpreterIOValidatorOps._

  def run[T](effect: Eff[MachinesStack, T]): Future[Either[MachinesError, T]] =
    database.withTransaction { implicit sessionDatabase =>
      FutureInterpretation.runAsync(effect.runDBIO.runEither)
    }(_.validateRight(_.valid))

}

object MachinesStackInterpreter {
  type MachinesStack = Fx.fx3[MachinesErrorEither[?], DBIO, TimedFuture]
}
