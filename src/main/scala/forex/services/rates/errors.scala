package forex.services.rates

object errors {

  sealed trait BusinessLogicError extends Exception
  object BusinessLogicError {
    final case class OneFrameLookupFailed(msg: String) extends BusinessLogicError
  }

}
