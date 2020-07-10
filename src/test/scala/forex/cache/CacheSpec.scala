package forex.cache

import cats.effect.IO
import cats.implicits._
import forex.config.{ApplicationConfig, Cache}
import forex.domain.Currency.{EUR, USD}
import forex.domain._
import forex.http.clients.oneframe.{ Algebra => HttpClient}
import forex.http.clients.oneframe.Protocol.OneFrameApiResponse
import forex.utils.Implicits._
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

import scala.concurrent.duration._
import scala.language.postfixOps

class CacheSpec extends AnyFlatSpec with MockFactory {

  val config: ApplicationConfig = ApplicationConfig(null, null, Cache(5 seconds, "localhost", 11211))

  val response: OneFrameApiResponse = OneFrameApiResponse(
    USD,
    EUR,
    new Price(100),
    new Bid(100),
    new Ask(100),
    Timestamp.now
  )

  val anotherResponse: OneFrameApiResponse = OneFrameApiResponse(
    USD,
    EUR,
    new Price(1020),
    new Bid(1010),
    new Ask(1030),
    Timestamp.now
  )

  val pair: Rate.Pair = Rate.Pair(USD, EUR)
  val anotherPair: Rate.Pair = Rate.Pair(EUR, USD)

  val mockedClient: HttpClient[IO] = mock[HttpClient[IO]]

  val cache: MemcachedCacheImpl[IO] = MemcachedCacheImpl[IO](config, mockedClient)

  it should "get rates from cache" in {

    (mockedClient.getRates(_ : Rate.Pair)).expects(pair).returning(IO(response)).once()

    val list = List.fill(100)(())
    val results = list.traverse(_ => cache.getRates(pair)).unsafeRunSync()
    results.size shouldBe 100

    assert(results.forall(_ == response))
  }

  it should "evict cache and add new values" in {

    (mockedClient.getRates(_ : Rate.Pair)).expects(anotherPair).onCall { _: Rate.Pair  => IO(response)}.once()

    val list = List.fill(100)(())
    val res = for {
      results <-  list.traverse(_ => cache.getRates(anotherPair))
      _ = (mockedClient.getRates(_ : Rate.Pair)).expects(anotherPair).onCall { _: Rate.Pair  => IO(anotherResponse)}.once()
      _ <- cache.evictCacheForRates(anotherPair)
      afterEvictResults <- list.traverse(_ => cache.getRates(anotherPair))
    } yield {
      assert (results.forall(_ == response))
      results.size shouldBe 100
      afterEvictResults.size shouldBe 100
      assert(afterEvictResults.forall(_ == anotherResponse))
    }

    res.unsafeRunSync()
  }

}
