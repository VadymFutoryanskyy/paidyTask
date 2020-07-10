package forex.http
package clients.oneframe

import forex.domain._
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

import scala.util.Try

object Protocol {

  final case class OneFrameApiResponse(
      from: Currency,
      to: Currency,
      price: Price,
      bid: Bid,
      ask: Ask,
      time_stamp: Timestamp
  )

  implicit val currencyDecoder: Decoder[Currency] = Decoder.decodeString.emapTry { str =>
    Try(Currency.fromString(str))
  }

  implicit val responseDecoder: Decoder[OneFrameApiResponse] =
    deriveDecoder[OneFrameApiResponse]

}
