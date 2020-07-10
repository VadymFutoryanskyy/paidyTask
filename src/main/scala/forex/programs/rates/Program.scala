package forex.programs.rates

import cats.effect.Sync
import forex.domain._
import forex.services.RatesService

class Program[F[_]: Sync](
    ratesService: RatesService[F]
) extends Algebra[F] {

  override def getRates(request: Protocol.RatesRequest): F[Rate] =
    ratesService.get(Rate.Pair(request.from, request.to))

  override def evictCacheForRates(request: Protocol.RatesRequest): F[Unit] =
    ratesService.evictCacheForRates(Rate.Pair(request.from, request.to))

}

object Program {

  def apply[F[_]: Sync](
      ratesService: RatesService[F]
  ): Algebra[F] = new Program[F](ratesService)

}
