package forex.config

import scala.concurrent.duration.FiniteDuration

case class ApplicationConfig(
    http: HttpConfig,
    oneframe: OneFrame,
    cache: Cache
)

case class HttpConfig(
    host: String,
    port: Int,
    timeout: FiniteDuration
)

case class OneFrame(
    host: String,
    port: Int,
    threads: Int,
    token: String
)

case class Cache(
    ttl: FiniteDuration,
    host: String,
    port: Int
)
