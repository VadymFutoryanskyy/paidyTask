package forex.services.rates.interpreters

import forex.services.rates.Algebra
import cats.Applicative
import cats.implicits._
import forex.domain.{ Price, Rate, Timestamp }

class OneFrameDummy[F[_]: Applicative] extends Algebra[F] {

  override def get(pair: Rate.Pair): F[Rate] =
    Rate(pair, Price(BigDecimal(100.01)), Timestamp.now).pure[F]

  override def evictCacheForRates(pair: Rate.Pair): F[Unit] = ().pure[F]
}
