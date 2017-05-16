package gg.uhc.website.seed

import org.databene.commons.converter.ThreadSafeConverter
import com.github.t3hnar.bcrypt._

class BcryptConverter extends ThreadSafeConverter[String, String](classOf[String], classOf[String]) {
  def convert(s: String): String = s.bcrypt
}
