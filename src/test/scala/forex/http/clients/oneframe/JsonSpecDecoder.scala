package forex.http.clients.oneframe

import java.time.OffsetDateTime

import forex.domain.Currency.{EUR, USD}
import forex.domain.{Ask, Bid, Price, Timestamp}
import forex.http.clients.oneframe.Protocol._
import io.circe.parser
import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

class JsonSpecDecoder extends AnyFlatSpec with MockFactory with EitherValues {

  it should "decode response to OneFrameResponse" in {
    val dateTime = OffsetDateTime.now()

    val expectedModel = OneFrameApiResponse(
      USD,
      EUR,
      new Price(12),
      Bid(0.1),
      Ask(5.3),
      Timestamp(dateTime)
    )


    val modelStrRepresentation = s"""
      |{
      |"from" : "USD",
      |"to" : "EUR",
      |"price" : 12,
      |"bid" : 0.1,
      |"ask" : 5.3,
      |"time_stamp" : "$dateTime"
      |}
      |""".stripMargin

    val decoded = parser.decode[OneFrameApiResponse](modelStrRepresentation)

    decoded.right.value shouldBe expectedModel

  }

}
