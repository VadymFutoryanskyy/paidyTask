package forex.http.errors

import cats.MonadError
import forex.services.rates.errors.BusinessLogicError.OneFrameLookupFailed
import org.http4s.dsl.Http4sDsl
import org.http4s.{ HttpRoutes, Response }

class ErrorsHandler[F[_]](implicit M: MonadError[F, Throwable]) extends Http4sDsl[F] {

  private val handler: Throwable => F[Response[F]] = {
    case OneFrameLookupFailed(msg) => BadRequest(msg)
    case err                       => InternalServerError(err.toString)
  }

  def handle(routes: HttpRoutes[F]): HttpRoutes[F] =
    RoutesHttpErrorHandler(routes)(handler)
}
