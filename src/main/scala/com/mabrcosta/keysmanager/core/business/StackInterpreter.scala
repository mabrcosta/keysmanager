package com.mabrcosta.keysmanager.core.business

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.util.DBIOEffect._
import com.mabrcosta.keysmanager.core.persistence.util.JdbcProfileAsyncDatabase
import com.mabrcosta.keysmanager.users.business.api.Stack
import javax.inject.Inject
import org.atnos.eff.concurrent.Scheduler
import org.atnos.eff.syntax.all._
import org.atnos.eff.{Eff, FutureInterpretation}

import scala.concurrent.{ExecutionContext, Future}

class StackInterpreter @Inject()(private val database: JdbcProfileAsyncDatabase,
                                 implicit val executionContext: ExecutionContext,
                                 implicit val scheduler: Scheduler) {

  def run[T](effect: Eff[Stack, T], uidOwner: UUID): Future[Either[api.Error, T]] =
    database.withTransaction { implicit sessionDatabase =>
      FutureInterpretation.runAsync(effect.runDBIO.runReader(uidOwner).runEither)
    }({
      case Left(_)  => false
      case Right(_) => true
    })

}
