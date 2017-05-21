package gg.uhc.website.validation

object EmailValidation {
  private[this] val emailRegex =
    """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  implicit class ValidateEmailString(s: String) {
    def isValidEmailFormat: Boolean = s match {
      case null                                          ⇒ false
      case e if e.trim.isEmpty                           ⇒ false
      case e if emailRegex.findFirstMatchIn(e).isDefined ⇒ true
      case _                                             ⇒ false
    }
  }
}
