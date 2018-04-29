package com.mabrcosta.keysmanager.users.business

import java.util.UUID

import com.mabrcosta.keysmanager.core.persistence.util.JdbcProfileAsyncDatabase
import com.mabrcosta.keysmanager.users.business.api.{Error, KeysStack}
import org.atnos.eff.{Eff, FutureInterpretation}
import org.atnos.eff.concurrent.Scheduler
import com.mabrcosta.keysmanager.core.persistence.util.DBIOEffect._
import javax.inject.Inject
import org.atnos.eff.syntax.all._

import scala.concurrent.{ExecutionContext, Future}

class KeysStackInterpreter @Inject()(private val database: JdbcProfileAsyncDatabase,
                                     implicit val executionContext: ExecutionContext,
                                     implicit val scheduler: Scheduler) {

  def run[T](effect: Eff[KeysStack, T], uidOwner: UUID): Future[Either[Error, T]] =
    database.withTransaction { implicit sessionDatabase =>
      FutureInterpretation.runAsync(effect.runDBIO.runReader(uidOwner).runEither)
    }({
      case Left(_)  => false
      case Right(_) => true
    })

}
