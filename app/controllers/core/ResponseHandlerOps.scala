package controllers.core

import play.api.Logger
import play.api.mvc.{Result, Results}

import scala.concurrent.{ExecutionContext, Future}

object ResponseHandlerOps extends Results {

  implicit class FutureResponseHandler[T](future: Future[T]) {
    def mapResponse(response: T => Result)(implicit ec: ExecutionContext): Future[Result] =
      handlerRecoverFuture(future.map(response))

    def flatMapResponse(response: T => Future[Result])(implicit ec: ExecutionContext): Future[Result] =
      handlerRecoverFuture(future.flatMap(response))

    private[this] def handlerRecoverFuture(future: Future[Result])(implicit ec: ExecutionContext): Future[Result] =
      future.recover({
        case ex: Throwable => {
          Logger.error(ex.getMessage, ex)
          InternalServerError(ex.getMessage)
        }
      })
  }

  implicit class EitherResponseHandler[TError, T, R](either: Either[TError, T]) {
    def eitherResponse(response: T => R)(implicit errorMapping: TError => R): R = either.fold(errorMapping, response)
  }

}
