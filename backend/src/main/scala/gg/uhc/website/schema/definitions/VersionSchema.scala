package gg.uhc.website.schema.definitions

import gg.uhc.website.model.{Match, Version}
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.ConnectionIOConverters._

import sangria.schema._

import scalaz.Scalaz._
import gg.uhc.website.schema.helpers.FieldHelpers._

object VersionSchema extends HasSchemaType[Version] with HasSchemaQueries {
  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      "versions",
      ListType(Type),
      arguments = Nil, // TODO replace with a connection for pagination purposes
      resolve = implicit ctx ⇒ ctx.ctx.versions.getAll,
      description = "Fetches all versions".some
    )
  )

  override lazy val Type: ObjectType[SchemaContext, Version] = ObjectType[SchemaContext, Version](
    name = "Version",
    description = "A choosable version for hosting",
    interfaces = interfaces[SchemaContext, Version](RelaySchema.nodeInterface),
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
        simpleConnectionField[Version, Match](
          name = "matches",
          target = MatchSchema.Type,
          description = "A list of games using this version",
          action = _.matches.getByVersionId,
          cursorFn = _.created.toString
        )
    )
  )
}
