package gg.uhc.website.seed

import com.github.javafaker.Faker
import org.databene.benerator.util.ThreadSafeNonNullGenerator

class DomainNameGenerator extends ThreadSafeNonNullGenerator[String] {
  private[this] val internet = new Faker().internet()

  override def generate(): String = internet.domainName()

  override def getGeneratedType: Class[String] = classOf[String]
}
