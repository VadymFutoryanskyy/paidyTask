package forex.http
package rates

import cats.effect.Sync
import cats.syntax.all._
import forex.programs.RatesProgram
import forex.programs.rates.{Protocol => RatesProgramProtocol}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.slf4j.{Logger, LoggerFactory}

class RatesHttpRoutes[F[_]: Sync](rates: RatesProgram[F]) extends Http4sDsl[F] {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  import Converters._
  import Protocol._
  import QueryParams._

  private[http] val prefixPath = "/rates"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root :? FromQueryParam(from) +& ToQueryParam(to) =>
      logger.info(s"Get rates for pair: $from $to")
      rates.getRates(RatesProgramProtocol.RatesRequest(from, to)).flatMap { rate =>
        Ok(rate.asGetApiResponse)
      }
    case PUT -> Root / "evict" :? FromQueryParam(from) +& ToQueryParam(to) =>
      logger.info(s"Evict cache for pair: $from $to")
      rates.evictCacheForRates(RatesProgramProtocol.RatesRequest(from, to)).flatMap(_ => Ok())

  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

}
