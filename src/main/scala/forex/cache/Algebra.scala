package forex.cache

import forex.domain.Rate
import forex.http.clients.oneframe.Protocol.OneFrameApiResponse

trait Algebra[F[_]] {

  def getRates(pair: Rate.Pair): F[OneFrameApiResponse]

  def evictCacheForRates(pair: Rate.Pair): F[Any]
}
