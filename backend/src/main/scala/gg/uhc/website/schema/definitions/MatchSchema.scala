package gg.uhc.website.schema.definitions

import gg.uhc.website.model.{Match, MatchScenario}
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.ConnectionIOConverters._
import gg.uhc.website.schema.helpers.FieldHelpers._
import gg.uhc.website.schema.scalars.InstantScalarTypeSupport._

import sangria.schema._

import scalaz.Scalaz._

object MatchSchema extends HasSchemaType[Match] with HasSchemaQueries {
  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      "matches",
      ListType(Type),
      arguments = Nil, // TODO replace with a connection for pagination purposes
      resolve = implicit ctx ⇒ ctx.ctx.matches.getAll,
      description = "Fetches all matches".some
    )
  )

  override lazy val Type: ObjectType[SchemaContext, Match] = ObjectType[SchemaContext, Match](
    name = "Match",
    description = "An individual match",
    interfaces = interfaces[SchemaContext, Match](RelaySchema.nodeInterface),
    fieldsFn = () ⇒
      fields[SchemaContext, Match](
        globalIdField,
        rawIdField,
        modifiedField,
        deletedField,
        createdField,
        Field(
          name = "size",
          fieldType = OptionType(IntType),
          description = "The size relating to the specific style".some,
          resolve = _.value.size
        ),
        Field(
          name = "starts",
          fieldType = InstantType,
          description = "When the match starts".some,
          resolve = _.value.starts
        ),
        // relations below here
        Field(
          name = "host",
          fieldType = UserSchema.Type,
          description = "The host for this match".some,
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.hostUserId)
        ),
        Field(
          name = "server",
          fieldType = ServerSchema.Type,
          description = "The server this match is hosted on".some,
          resolve = ctx ⇒ Fetchers.servers.defer(ctx.value.serverId)
        ),
        Field(
          name = "version",
          fieldType = VersionSchema.Type,
          description = "The version that is being hosted".some,
          resolve = ctx ⇒ Fetchers.versions.defer(ctx.value.versionId)
        ),
        Field(
          name = "style",
          fieldType = StyleSchema.Type,
          description = "The team style being hosted".some,
          resolve = ctx ⇒ Fetchers.styles.defer(ctx.value.styleId)
        ),
        // Connections below here
        simpleConnectionField[Match, MatchScenario](
          name = "scenarios",
          target = MatchScenarioSchema.Type,
          description = "Scenarios for this match",
          action = _.matchScenarios.getByMatchId,
          cursorFn = _.scenarioId.toString
        )
    )
  )
}
