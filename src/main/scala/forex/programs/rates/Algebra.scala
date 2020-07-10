package forex.programs.rates

import forex.domain.Rate

trait Algebra[F[_]] {
  def getRates(request: Protocol.RatesRequest): F[Rate]

  def evictCacheForRates(request: Protocol.RatesRequest): F[Unit]
}
