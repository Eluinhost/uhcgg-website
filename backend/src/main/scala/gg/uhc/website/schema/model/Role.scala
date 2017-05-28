package gg.uhc.website.schema.model

import sangria.execution.deferred.HasId

object Role {
  implicit val hasId: HasId[Role, Int] = HasId(_.id)
}

case class Role(id: Int, name: String, permissions: List[String]) extends IdentificationFields[Int]
