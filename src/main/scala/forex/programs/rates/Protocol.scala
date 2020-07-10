package forex.programs.rates

import forex.domain.Currency

object Protocol {

  final case class RatesRequest(
      from: Currency,
      to: Currency
  )

}
