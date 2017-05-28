package gg.uhc.website.schema.model

import sangria.execution.deferred.HasId

object Version {
  implicit val hasId: HasId[Version, Int] = HasId(_.id)
}

case class Version(id: Int, name: String, live: Boolean) extends IdentificationFields[Int]
