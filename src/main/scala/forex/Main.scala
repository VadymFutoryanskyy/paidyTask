package forex

import cats.effect._
import cats.syntax.functor._
import forex.config._
import forex.utils.ExecutionContexts
import fs2.Stream
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.client.blaze.BlazeClientBuilder
import scalacache.Mode
import cats.effect.IO
import forex.http.errors.ErrorsHandler
import forex.utils.Implicits._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    new Application[IO].stream.compile.drain.as(ExitCode.Success)

}

class Application[F[_]: ConcurrentEffect: Timer: Mode: ErrorsHandler] {

  def stream: Stream[F, Unit] =
    for {
      config <- Config.stream("app")
      client <- Stream.resource(BlazeClientBuilder[F](ExecutionContexts.getHttpClientThreadPool(config)).resource)
      module = new Module[F](config, client)
      _ <- BlazeServerBuilder[F]
            .bindHttp(config.http.port, config.http.host)
            .withHttpApp(module.httpApp)
            .serve

    } yield ()

}
