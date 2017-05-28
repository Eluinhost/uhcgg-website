package gg.uhc.website.schema.model

import sangria.execution.deferred.HasId

object Style {
  implicit val hasId: HasId[Style, Int] = HasId(_.id)
}

case class Style(id: Int, shortName: String, fullName: String, description: String, requiresSize: Boolean)
    extends IdentificationFields[Int]
