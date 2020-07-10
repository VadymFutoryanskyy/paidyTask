package forex.http.clients.oneframe

import cats.effect.Sync
import cats.implicits._
import forex.config.ApplicationConfig
import forex.domain.Rate
import forex.http.clients._
import forex.http.clients.oneframe.Protocol._
import forex.services.rates.errors.BusinessLogicError.OneFrameLookupFailed
import org.http4s.Method.GET
import org.http4s.Uri.RegName
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s._
import org.slf4j.{ Logger, LoggerFactory }

class OneFrameHttpClient[F[_]: Sync](client: Client[F], config: ApplicationConfig)
    extends Http4sClientDsl[F]
    with Algebra[F] {

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  private val getRatesPath            = "/rates"
  private val getRatesQueryParam      = "pair"
  private val tokenHeader: Header.Raw = Header("token", config.oneframe.token)
  private val getRatesUri: Uri = Uri(
    scheme = Some(Uri.Scheme.http),
    authority = Some(Uri.Authority(host = RegName(config.oneframe.host), port = Some(config.oneframe.port))),
    path = getRatesPath
  )

  def getRates(pair: Rate.Pair): F[OneFrameApiResponse] = {

    logger.info(s"Fetching rates for pair: $pair")
    val request = GET(getRatesUri.withQueryParam(getRatesQueryParam, pair.toString), tokenHeader)
    client.expect[List[OneFrameApiResponse]](request).flatMap { response =>
      logger.info(s"Got rates $response")
      response.fromListResponse[F](
        OneFrameLookupFailed("At least one result is expected from one frame service, but got an empty list")
      )
    }
  }
}

object OneFrameHttpClient {

  def apply[F[_]: Sync](client: Client[F], config: ApplicationConfig): OneFrameHttpClient[F] =
    new OneFrameHttpClient(client, config)
}
