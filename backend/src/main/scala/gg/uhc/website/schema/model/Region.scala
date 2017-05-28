package gg.uhc.website.schema.model

import sangria.execution.deferred.HasId

object Region {
  implicit val hasId: HasId[Region, Int] = HasId(_.id)
}

case class Region(id: Int, short: String, long: String) extends IdentificationFields[Int]
