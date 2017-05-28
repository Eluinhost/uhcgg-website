package gg.uhc.website.schema.definitions

import gg.uhc.website.model.Version
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.SchemaIds._
import sangria.schema._

import scalaz.Scalaz._

object VersionSchema extends SchemaDefinition[Version] with SchemaQueries with SchemaSupport {
  private val idArg  = Argument(name = "id", argumentType = IntType, description = "ID to match")
  private val idsArg = Argument(name = "ids", argumentType = ListInputType(IntType), description = "IDs to match")

  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "versionById",
      fieldType = OptionType(Type),
      arguments = idArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.versions.deferOpt(idArg.resolve),
      description = "Looks up a version with the given id".some
    ),
    Field(
      name = "versionsByIds",
      fieldType = ListType(Type),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = implicit ctx ⇒ Fetchers.versions.deferSeqOpt(idsArg.resolve),
      description = "Looks up versions with the given ids".some
    ),
    Field(
      "versions",
      ListType(Type),
      arguments = Nil, // TODO pagination
      resolve = implicit ctx ⇒ ctx.ctx.versions.getAll,
      description = "Fetches all versions".some
    )
  )

  override lazy val Type: ObjectType[Unit, Version] = ObjectType(
    name = "Version",
    description = "A choosable version for hosting",
    fieldsFn = () ⇒
      idFields[Version, Int] ++ fields[Unit, Version](
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
        //////////////////////////
        // Relations below here //
        //////////////////////////
        Field(
          name = "matches",
          fieldType = ListType(MatchSchema.Type), // TODO pagination + a way to filter out old ones
          description = "A list of games using this version".some,
          resolve = ctx ⇒ Fetchers.matches.deferRelSeq(Relations.matchByVersionId, ctx.value.id)
        )
    )
  )
}
