package gg.uhc.website.schema.definitions

import java.time.Instant
import java.util.UUID

import gg.uhc.website.model.{Match, Style}
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.ConnectionIOConverters._
import gg.uhc.website.schema.helpers.FieldHelpers._
import gg.uhc.website.schema.scalars.InstantScalarTypeSupport._
import sangria.schema._

import scalaz.Scalaz._

object StyleSchema extends HasSchemaType[Style] with HasSchemaQueries {
  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      "styles",
      ListType(Type), // TODO replace with a connection for pagination purposes
      arguments = Nil,
      resolve = implicit ctx ⇒ ctx.ctx.styles.getAll,
      description = "Fetches all styles".some
    )
  )

  override lazy val Type: ObjectType[SchemaContext, Style] = ObjectType[SchemaContext, Style](
    name = "Style",
    description = "A team style for a match",
    interfaces = interfaces[SchemaContext, Style](RelaySchema.nodeInterface),
    fieldsFn = () ⇒
      fields[SchemaContext, Style](
        globalIdField,
        rawIdField,
        Field(
          name = "shortName",
          fieldType = StringType,
          description = "A 'short' format template string".some,
          resolve = _.value.shortName
        ),
        Field(
          name = "fullName",
          fieldType = StringType,
          description = "A 'full' format template string".some,
          resolve = _.value.fullName
        ),
        Field(
          name = "description",
          fieldType = StringType,
          description = "The full description explaining how this style works".some,
          resolve = _.value.description
        ),
        Field(
          name = "requiresSize",
          fieldType = BooleanType,
          description = "Whether the style requires a team size to also be provided or not".some,
          resolve = _.value.requiresSize
        ),
        // Connections below here
        relationshipField[Style, Match, UUID, Instant](
          name = "matches",
          targetType = MatchSchema.Type,
          description = "A list of games using this style",
          action = _.matches.getByStyleId,
          cursorFn = (m: Match) ⇒ m.created,
          idFn = (s: Style) ⇒ s.uuid
        )
    )
  )
}
