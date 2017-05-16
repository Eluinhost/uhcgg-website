package gg.uhc.website.seed

import org.databene.benerator.distribution.sequence.RandomIntegerGenerator
import org.databene.benerator.util.ThreadSafeNonNullGenerator

class Ip4AddressGenerator extends ThreadSafeNonNullGenerator[String] {
  private[this] val integers = new RandomIntegerGenerator(0, 255, 1)

  override def generate(): String = (1 to 4).map(_ ⇒ integers.generate()).mkString(".")

  override def getGeneratedType: Class[String] = classOf[String]
}
