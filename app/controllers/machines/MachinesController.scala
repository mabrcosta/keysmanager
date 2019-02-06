package controllers.machines

import com.mabrcosta.keysmanager.access.business.api.AccessService
import com.mabrcosta.keysmanager.machines.business.api.{MachineNotFound, MachinesError}
import com.mabrcosta.keysmanager.users.data.Key
import controllers.machines.interpreters.MachinesStackInterpreter
import controllers.machines.interpreters.MachinesStackInterpreter.MachinesStack
import javax.inject.Inject
import org.atnos.eff.Eff
import play.api.mvc._
import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}

class MachinesController @Inject()(private[this] val cc: ControllerComponents,
                                   private[this] val accessService: AccessService[DBIO, DBIO],
                                   private[this] val machinesStackInterpreter: MachinesStackInterpreter,
                                   implicit val executionContext: ExecutionContext)
    extends AbstractController(cc) {

  def authorizedKeys(hostname: String): Action[AnyContent] = Action.async { implicit request =>
    handleStackResponse(accessService.getAuthorizedKeys[MachinesStack](hostname))(keys => Ok(buildAuthorizedKeys(keys)))
  }

  private[this] def buildAuthorizedKeys(keys: Seq[Key]): String = keys.map(_.value).mkString("\n")

  implicit val usersErrorMapping: MachinesError => Result = {
    case MachineNotFound(message) => NotFound(message)
  }

  private[this] def handleStackResponse[T](effect: Eff[MachinesStack, T])(response: T => Result): Future[Result] = {

    import controllers.core.ResponseHandlerOps._
    machinesStackInterpreter.run(effect).mapResponse(_.eitherResponse(response))
  }

}
