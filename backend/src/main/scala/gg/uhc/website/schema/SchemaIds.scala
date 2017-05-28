package gg.uhc.website.schema

import java.util.UUID

import gg.uhc.website.model._
import sangria.execution.deferred.HasId

trait SchemaIds {
  implicit val banHasId: HasId[Ban, Long] = HasId(_.id)
  implicit val matchHasId: HasId[Match, Long] = HasId(_.id)
  implicit val networkHasId: HasId[Network, Long] = HasId(_.id)
  implicit val regionHasId: HasId[Region, Int] = HasId(_.id)
  implicit val roleHasId: HasId[Role, Int] = HasId(_.id)
  implicit val scenarioHasId: HasId[Scenario, Long] = HasId(_.id)
  implicit val serverHasId: HasId[Server, Long] = HasId(_.id)
  implicit val styleHasId: HasId[Style, Int] = HasId(_.id)
  implicit val userHasId: HasId[User, UUID] = HasId(_.id)
  implicit val versionHasId: HasId[Version, Int] = HasId(_.id)
}

object SchemaIds extends SchemaIds