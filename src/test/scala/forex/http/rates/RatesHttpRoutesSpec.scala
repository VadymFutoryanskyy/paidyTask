package forex.http.rates


import cats.effect._
import forex.domain.Currency.{EUR, USD}
import forex.domain.{Price, Rate, Timestamp}
import forex.http.rates.Protocol.GetApiResponse
import forex.programs.RatesProgram
import forex.programs.rates.Protocol.RatesRequest
import io.circe._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec

class RatesHttpRoutesSpec extends AnyFlatSpec with MockFactory {

  val programMock: RatesProgram[IO] = mock[RatesProgram[IO]]

  it should "get rates from USD to EUR" in {

    val dateTime = Timestamp.now
    val price = Price(2.5)
    val expectedJson = GetApiResponse(
      USD,
      EUR,
      price,
      dateTime
    ).asJson

    val request = RatesRequest(USD, EUR)
    val response = Rate(Rate.Pair(USD, EUR), price, dateTime)

    (programMock.getRates(_ : RatesRequest)).expects(request).returning(IO(response)).once()

    val routes = new RatesHttpRoutes(programMock)
    val requestUri = Uri(
      path = routes.prefixPath,
      query = Query.fromMap(Map("from"-> Seq("USD"), "to" -> Seq("EUR")))
    )
    val actualResponse = routes.routes.orNotFound.run(
      Request(method = Method.GET, uri = requestUri)
    )

    assert(check[Json](actualResponse, Status.Ok, Some(expectedJson)))

  }

  it should "evict cache for USD and EUR" in {

    val request = RatesRequest(USD, EUR)

    (programMock.evictCacheForRates(_ : RatesRequest)).expects(request).returning(IO(())).once()

    val routes = new RatesHttpRoutes(programMock)
    val requestUri = Uri(
      path = routes.prefixPath + "/evict",
      query = Query.fromMap(Map("from"-> Seq("USD"), "to" -> Seq("EUR")))
    )
    val actualResponse = routes.routes.orNotFound.run(
      Request(method = Method.PUT, uri = requestUri)
    )

    assert(check[Json](actualResponse, Status.Ok, None))

  }

  private def check[A](actual:        IO[Response[IO]],
               expectedStatus: Status,
               expectedBody:   Option[A])(
                implicit ev: EntityDecoder[IO, A]
              ): Boolean =  {
    val actualResp         = actual.unsafeRunSync
    val statusCheck        = actualResp.status == expectedStatus
    val bodyCheck          = expectedBody.fold[Boolean](
      actualResp.body.compile.toVector.unsafeRunSync.isEmpty)(
      expected => actualResp.as[A].unsafeRunSync == expected
    )
    statusCheck && bodyCheck
  }

}
