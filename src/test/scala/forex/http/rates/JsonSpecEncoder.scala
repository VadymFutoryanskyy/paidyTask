package forex.http.rates

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import forex.domain.Currency.{EUR, USD}
import forex.domain.{Price, Timestamp}
import forex.http.rates.Protocol.{GetApiResponse, _}
import io.circe.syntax._
import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

class JsonSpecEncoder extends AnyFlatSpec with MockFactory with EitherValues {

  it should "encode GetApiResponse to string" in {
    val dateTime = OffsetDateTime.now()
    val formattedDate = dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    val apiResponse = GetApiResponse(
      USD,
      EUR,
      new Price(20),
      Timestamp(dateTime)
    ).asJson.toString()


    val expectedStr = s"""{
                                    |  "from" : "USD",
                                    |  "to" : "EUR",
                                    |  "price" : 20,
                                    |  "timestamp" : "$formattedDate"
                                    |}""".stripMargin

    val escapedExpectedString = StringContext treatEscapes expectedStr

    apiResponse shouldBe escapedExpectedString

  }

}

