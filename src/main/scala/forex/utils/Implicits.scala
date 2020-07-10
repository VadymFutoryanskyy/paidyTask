package forex.utils

import cats.effect.IO
import forex.http.errors.ErrorsHandler
import scalacache.Mode

object Implicits {

  implicit val mode: Mode[IO]                          = scalacache.CatsEffect.modes.async
  implicit val userHttpErrorHandler: ErrorsHandler[IO] = new ErrorsHandler[IO]

}
