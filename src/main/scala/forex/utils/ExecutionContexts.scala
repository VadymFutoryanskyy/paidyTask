package forex.utils

import java.util.concurrent.Executors

import forex.config.ApplicationConfig

import scala.concurrent.{ ExecutionContext, ExecutionContextExecutor }

object ExecutionContexts {

  def getHttpClientThreadPool(config: ApplicationConfig): ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(config.oneframe.threads))

}
