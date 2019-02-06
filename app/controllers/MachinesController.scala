package controllers
import com.mabrcosta.keysmanager.core.business.api.{NotFound => NotFoundError}
import com.mabrcosta.keysmanager.core.business.{StackInterpreter, api}
import com.mabrcosta.keysmanager.machines.business.api.MachinesService
import com.mabrcosta.keysmanager.users.business.api.Stack
import com.mabrcosta.keysmanager.users.data.Key
import javax.inject.Inject
import org.atnos.eff.Eff
import play.api.libs.json.{Json, OWrites}
import play.api.mvc._
import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}

class MachinesController @Inject()(private[this] val cc: ControllerComponents,
                                   private[this] val machinesService: MachinesService[DBIO, DBIO],
                                   private[this] val stackInterpreter: StackInterpreter,
                                   implicit val executionContext: ExecutionContext)
    extends AbstractController(cc) {

  implicit val keyFormat: OWrites[Key] = Json.writes[Key]

  def authorizedKeys(hostname: String): Action[AnyContent] = Action.async { implicit request =>
    handleStackResponse(machinesService.getAuthorizedKeys[Stack](hostname))(keys => Ok(Json.toJson(keys)))
  }

  implicit val usersErrorMapping: api.Error => Result = {
    case NotFoundError(_) => BadRequest("Not Found")
  }

  private[this] def handleStackResponse[T](effect: Eff[Stack, T])(response: T => Result): Future[Result] = {

    import controllers.core.ResponseHandlerOps._
    stackInterpreter.run(effect, null).mapResponse(_.eitherResponse(response))
  }

}
