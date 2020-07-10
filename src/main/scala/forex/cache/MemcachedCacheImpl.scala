package forex.cache

import forex.config.ApplicationConfig
import forex.domain.Rate
import forex.http.OneFrameClient
import forex.http.clients.oneframe.Protocol.OneFrameApiResponse
import scalacache._
import scalacache.memcached._
import scalacache.serialization.binary._

class MemcachedCacheImpl[F[_]: Mode](config: ApplicationConfig, client: OneFrameClient[F])(
    implicit val oneFrameApiCache: Cache[OneFrameApiResponse]
) extends Algebra[F] {

  override def getRates(pair: Rate.Pair): F[OneFrameApiResponse] =
    cachingF[F, OneFrameApiResponse](pair)(Some(config.cache.ttl)) {
      client.getRates(pair)
    }

  override def evictCacheForRates(pair: Rate.Pair): F[Any] =
    remove(pair)

}

object MemcachedCacheImpl {

  def apply[F[_]: Mode](config: ApplicationConfig, client: OneFrameClient[F]): MemcachedCacheImpl[F] = {
    implicit val oneFrameApiCache: Cache[OneFrameApiResponse] = MemcachedCache(
      s"${config.cache.host}:${config.cache.port}"
    )
    new MemcachedCacheImpl[F](config, client)
  }

}
