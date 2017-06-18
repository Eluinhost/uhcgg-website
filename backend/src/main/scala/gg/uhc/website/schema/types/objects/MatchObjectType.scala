package gg.uhc.website.schema.types.objects

import java.util.UUID

import gg.uhc.website.model.{Match, MatchScenario}
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.FieldHelpers._
import gg.uhc.website.schema.types.scalars.InstantScalarType._
import gg.uhc.website.schema.types.scalars.UuidScalarType._
import gg.uhc.website.schema.{Fetchers, SchemaContext}
import sangria.schema._

import scalaz.Scalaz._

object MatchObjectType extends HasObjectType[Match] {
  override lazy val Type: ObjectType[SchemaContext, Match] = ObjectType[SchemaContext, Match](
    name = "Match",
    description = "An individual match",
    interfaces = interfaces[SchemaContext, Match](RelayDefinitions.nodeInterface),
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
          fieldType = UserObjectType.Type,
          description = "The host for this match".some,
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.hostUserId)
        ),
        Field(
          name = "server",
          fieldType = ServerObjectType.Type,
          description = "The server this match is hosted on".some,
          resolve = ctx ⇒ Fetchers.servers.defer(ctx.value.serverId)
        ),
        Field(
          name = "version",
          fieldType = VersionObjectType.Type,
          description = "The version that is being hosted".some,
          resolve = ctx ⇒ Fetchers.versions.defer(ctx.value.versionId)
        ),
        Field(
          name = "style",
          fieldType = StyleObjectType.Type,
          description = "The team style being hosted".some,
          resolve = ctx ⇒ Fetchers.styles.defer(ctx.value.styleId)
        ),
        // Connections below here
        relationshipField[Match, MatchScenario, UUID, UUID](
          name = "scenarios",
          targetType = MatchScenarioObjectType.Type,
          description = "Scenarios for this match",
          action = _.matchScenarios.getByMatchId,
          cursorFn = (ms: MatchScenario) => ms.scenarioId,
          idFn = (m: Match) ⇒ m.uuid
        )
    )
  )
}
