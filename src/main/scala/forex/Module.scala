package forex

import cats.effect.{ ConcurrentEffect, Timer }
import forex.config.ApplicationConfig
import forex.http.rates.RatesHttpRoutes
import forex.cache._
import forex.http.OneFrameClient
import forex.http.errors.ErrorsHandler
import forex.programs._
import forex.services._
import org.http4s._
import org.http4s.client.Client
import org.http4s.implicits._
import org.http4s.server.middleware.{ AutoSlash, Timeout }
import scalacache.Mode

class Module[F[_]: ConcurrentEffect: Timer: Mode](config: ApplicationConfig, client: Client[F])(
    implicit H: ErrorsHandler[F]
) {

  private val oneFrameHttpClient: OneFrameClient[F] = OneFrameClient[F](client, config)

  private val oneFrameCache: OneFrameCache[F] = OneFrameCache[F](config, oneFrameHttpClient)

  private val ratesService: RatesService[F] = RatesServices.live[F](oneFrameCache)

  private val ratesProgram: RatesProgram[F] = RatesProgram[F](ratesService)

  private val ratesHttpRoutes: HttpRoutes[F] = new RatesHttpRoutes[F](ratesProgram).routes

  type PartialMiddleware = HttpRoutes[F] => HttpRoutes[F]
  type TotalMiddleware   = HttpApp[F] => HttpApp[F]

  private val routesMiddleware: PartialMiddleware = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    }
  }

  private val appMiddleware: TotalMiddleware = { http: HttpApp[F] =>
    Timeout(config.http.timeout)(http)
  }

  private val http: HttpRoutes[F]              = ratesHttpRoutes
  private val handledHttpRoutes: HttpRoutes[F] = H.handle(http)

  val httpApp: HttpApp[F] = appMiddleware(routesMiddleware(handledHttpRoutes).orNotFound)

}
