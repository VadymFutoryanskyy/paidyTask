package forex.http

import cats.effect.Sync
import forex.http.clients.oneframe.Protocol.OneFrameApiResponse
import forex.services.rates.errors.BusinessLogicError

package object clients {

  implicit class FromListResponseConverter(val response: List[OneFrameApiResponse]) extends AnyVal {

    def fromListResponse[F[_]](error: => BusinessLogicError)(implicit F: Sync[F]): F[OneFrameApiResponse] =
      response.headOption.fold(F.raiseError[OneFrameApiResponse](error)) { oneFrameApiResponse =>
        F.pure(oneFrameApiResponse)
      }
  }

}
