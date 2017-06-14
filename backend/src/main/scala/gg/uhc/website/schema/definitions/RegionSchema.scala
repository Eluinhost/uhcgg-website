package gg.uhc.website.schema.definitions

import gg.uhc.website.model.Region
import gg.uhc.website.schema.SchemaContext
import sangria.schema._

import scalaz.Scalaz._

object RegionSchema extends SchemaDefinition[Region] with SchemaQueries with SchemaSupport {
  private val idArg  = Argument(name = "id", argumentType = StringType, description = "ID to match")
  private val idsArg = Argument(name = "ids", argumentType = ListInputType(StringType), description = "IDs to match")

  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "regionById",
      fieldType = OptionType(Type),
      arguments = idArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.regions.deferOpt(idArg.resolve),
      description = "Looks up a region with the given id".some
    ),
    Field(
      name = "regionsByIds",
      fieldType = ListType(Type),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = implicit ctx ⇒ Fetchers.regions.deferSeqOpt(idsArg.resolve),
      description = "Looks up regions with the given ids".some
    ),
    Field(
      "regions",
      ListType(Type),
      arguments = Nil, // TODO pagination
      resolve = implicit ctx ⇒ ctx.ctx.regions.getAll,
      description = "Fetches all regions".some
    )
  )

  override lazy val Type: ObjectType[Unit, Region] = ObjectType(
    name = "Region",
    description = "A choosable region for hosting in",
    interfaces = interfaces[Unit, Region](RelaySchema.nodeInterface),
    fieldsFn = () ⇒
      idFields[Region] ++ fields[Unit, Region](
        Field(
          name = "short",
          fieldType = StringType,
          description = "The 'short' verison of the name".some,
          resolve = _.value.short
        ),
        Field(
          name = "long",
          fieldType = StringType,
          description = "The 'full' version of the name".some,
          resolve = _.value.long
        ),
        //////////////////////////
        // Relations below here //
        //////////////////////////
        Field(
          name = "servers",
          fieldType = ListType(ServerSchema.Type),
          description = "List of servers in this region".some,
          resolve = ctx ⇒ Fetchers.servers.deferRelSeq(Relations.serverByRegionId, ctx.value.id)
        )
    )
  )
}
