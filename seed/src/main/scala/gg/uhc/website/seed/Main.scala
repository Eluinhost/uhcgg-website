package gg.uhc.website.seed

import org.databene.benerator.engine.{DefaultBeneratorContext, DescriptorRunner}

object Main extends App {
  val context = new DefaultBeneratorContext()
  val runner  = new DescriptorRunner("benerator.xml", context)
  runner.run()
}
