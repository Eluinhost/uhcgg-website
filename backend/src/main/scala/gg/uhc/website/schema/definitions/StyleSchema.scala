package gg.uhc.website.schema.definitions

import gg.uhc.website.model.Style
import gg.uhc.website.schema.SchemaContext
import sangria.schema._

import scalaz.Scalaz._

object StyleSchema extends SchemaDefinition[Style] with SchemaQueries with SchemaSupport {
  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "styleById",
      fieldType = OptionType(Type),
      arguments = idArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.styles.deferOpt(idArg.resolve),
      description = "Looks up a style with the given id".some
    ),
    Field(
      name = "stylesByIds",
      fieldType = ListType(Type),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = implicit ctx ⇒ Fetchers.styles.deferSeqOpt(idsArg.resolve),
      description = "Looks up styles with the given ids".some
    ),
    Field(
      "styles",
      ListType(Type), // TODO pagination
      arguments = Nil,
      resolve = implicit ctx ⇒ ctx.ctx.styles.getAll,
      description = "Fetches all styles".some
    )
  )

  override lazy val Type: ObjectType[Unit, Style] = ObjectType(
    name = "Style",
    description = "A team style for a match",
    interfaces = interfaces[Unit, Style](RelaySchema.nodeInterface),
    fieldsFn = () ⇒
      idFields[Style] ++ fields[Unit, Style](
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
        // relations below here
        Field(
          name = "matches",
          fieldType = ListType(MatchSchema.Type), // TODO pagination + a way to filter out old ones
          description = "A list of games using this style".some,
          resolve = ctx ⇒ Fetchers.matches.deferRelSeq(Relations.matchByStyleId, ctx.value.uuid)
        )
    )
  )
}
