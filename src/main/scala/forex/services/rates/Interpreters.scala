package forex.services.rates

import cats.Applicative
import cats.effect.Sync
import forex.cache.OneFrameCache
import interpreters._

object Interpreters {
  def dummy[F[_]: Applicative](): Algebra[F] = new OneFrameDummy[F]()

  def live[F[_]: Sync](cache: OneFrameCache[F]): Algebra[F] = new OneFrameLive[F](cache)
}
