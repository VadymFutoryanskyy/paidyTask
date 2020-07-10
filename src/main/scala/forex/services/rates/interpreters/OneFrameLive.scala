package forex.services.rates.interpreters

import cats.effect.Sync
import cats.implicits._
import forex.cache.OneFrameCache
import forex.domain.{ Rate, Timestamp }
import forex.services.rates.Algebra

class OneFrameLive[F[_]: Sync](cache: OneFrameCache[F]) extends Algebra[F] {

  override def get(pair: Rate.Pair): F[Rate] =
    cache.getRates(pair).map { response =>
      Rate(pair, response.price, Timestamp.now)
    }

  override def evictCacheForRates(pair: Rate.Pair): F[Unit] =
    cache.evictCacheForRates(pair).map(_ => ())
}
