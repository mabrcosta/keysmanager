package controllers
import com.mabrcosta.keysmanager.access.business.api.AccessService
import com.mabrcosta.keysmanager.core.business.api.{NotFound => NotFoundError}
import com.mabrcosta.keysmanager.core.business.{StackInterpreter, api}
import com.mabrcosta.keysmanager.users.business.api.Stack
import com.mabrcosta.keysmanager.users.data.Key
import javax.inject.Inject
import org.atnos.eff.Eff
import play.api.mvc._
import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}

class MachinesController @Inject()(private[this] val cc: ControllerComponents,
                                   private[this] val accessService: AccessService[DBIO, DBIO],
                                   private[this] val stackInterpreter: StackInterpreter,
                                   implicit val executionContext: ExecutionContext)
    extends AbstractController(cc) {

  def authorizedKeys(hostname: String): Action[AnyContent] = Action.async { implicit request =>
    handleStackResponse(accessService.getAuthorizedKeys[Stack](hostname))(keys => Ok(buildAuthorizedKeys(keys)))
  }

  private[this] def buildAuthorizedKeys(keys: Seq[Key]): String = keys.map(_.value).mkString("\n")

  implicit val usersErrorMapping: api.Error => Result = {
    case NotFoundError(_) => BadRequest("Not Found")
  }

  private[this] def handleStackResponse[T](effect: Eff[Stack, T])(response: T => Result): Future[Result] = {

    import controllers.core.ResponseHandlerOps._
    stackInterpreter.run(effect, null).mapResponse(_.eitherResponse(response))
  }

}
