package gg.uhc.website.schema.definitions

import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.model.Match
import sangria.schema._

import scalaz.Scalaz._

object MatchSchema extends SchemaDefinition[Match] with SchemaQueries with SchemaSupport {
  private val idArg  = Argument(name = "id", argumentType = LongType, description = "ID to match")
  private val idsArg = Argument(name = "ids", argumentType = ListInputType(LongType), description = "IDs to match")

  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "matchById",
      fieldType = OptionType(Type),
      arguments = idArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.matches.deferOpt(idArg.resolve),
      description = "Looks up a match with the given id".some
    ),
    Field(
      name = "matchesByIds",
      fieldType = ListType(Type),
      arguments = idsArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.matches.deferSeqOpt(idsArg.resolve),
      description = "Looks up matches with the given ids".some
    ),
    Field(
      "matches",
      ListType(Type),
      arguments = Nil, // TODO pagination + filters
      resolve = implicit ctx ⇒ ctx.ctx.matches.getAll,
      description = "Fetches all matches".some
    )
  )
  override lazy val Type: ObjectType[Unit, Match] = ObjectType(
    name = "Match",
    description = "An individual match",
    fieldsFn = () ⇒
      idFields[Match, Long] ++ modificationTimesFields ++ deletedFields ++ fields[Unit, Match](
        Field(
          name = "size",
          fieldType = OptionType(IntType),
          description = "The size relating to the specific style".some,
          resolve = _.value.size
        ),
        Field(
          name = "starts",
          fieldType = DateType,
          description = "When the match starts".some,
          resolve = _.value.starts
        ),
        // relations below here
        Field(
          name = "host",
          fieldType = UserSchema.Type,
          description = "The host for this match".some,
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.host)
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
        Field(
          name = "scenarios",
          fieldType = ListType(MatchScenarioSchema.Type), // TODO pagination
          description = "Scenarios for this match".some,
          resolve = ctx ⇒ Fetchers.matchScenarios.deferRelSeq(Relations.matchScenarioByMatchId, ctx.value.id)
        )
    )
  )
}
