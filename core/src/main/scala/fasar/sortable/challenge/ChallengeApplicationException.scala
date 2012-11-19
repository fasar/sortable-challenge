package fasar.sortable.challenge

/** @see java.lang.Exception
  */
class ChallengeApplicationException(message: String, cause: Throwable) extends Exception(message, cause) {
  def this(s: String) {
    this(s, null)
  }

  def this(cause: Throwable) {
    this("", cause)
  }

  def this() {
    this("", null)
  }

}
