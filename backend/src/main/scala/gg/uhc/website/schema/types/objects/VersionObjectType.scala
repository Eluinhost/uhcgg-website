package gg.uhc.website.schema.types.objects

import java.time.Instant
import java.util.UUID

import gg.uhc.website.model.{Match, Version}
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.FieldHelpers._
import gg.uhc.website.schema.types.scalars.InstantScalarType._
import sangria.schema._

import scalaz.Scalaz._

object VersionObjectType extends HasObjectType[Version] {
  override lazy val Type: ObjectType[SchemaContext, Version] = ObjectType[SchemaContext, Version](
    name = "Version",
    description = "A choosable version for hosting",
    interfaces = interfaces[SchemaContext, Version](RelayDefinitions.nodeInterface),
    fieldsFn = () ⇒
      fields[SchemaContext, Version](
        globalIdField,
        rawIdField,
        Field(
          name = "name",
          fieldType = StringType,
          description = "The display name of this version".some,
          resolve = _.value.name
        ),
        Field(
          name = "live",
          fieldType = BooleanType,
          description = "Whether the item is 'live' or not. Only live versions can be picked for new matches".some,
          resolve = _.value.live
        ),
        // Connections below here
        relationshipField[Version, Match, UUID, Instant](
          name = "matches",
          targetType = MatchObjectType.Type,
          description = "A list of games using this version",
          action = _.matches.getByVersionId,
          cursorFn = (m: Match) ⇒ m.created,
          idFn = (v: Version) ⇒ v.uuid
        )
    )
  )
}
