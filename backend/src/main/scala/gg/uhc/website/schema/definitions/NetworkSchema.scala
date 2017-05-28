package gg.uhc.website.schema.definitions

import gg.uhc.website.model.Network
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.SchemaIds._
import sangria.schema._

import scalaz.Scalaz._

object NetworkSchema extends SchemaDefinition[Network] with SchemaQueries with SchemaSupport {
  private val idArg  = Argument(name = "id", argumentType = LongType, description = "ID to match")
  private val idsArg = Argument(name = "ids", argumentType = ListInputType(LongType), description = "IDs to match")

  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "networkById",
      fieldType = OptionType(Type),
      arguments = idArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.networks.deferOpt(idArg.resolve),
      description = "Looks up a version with the given id".some
    ),
    Field(
      name = "networksByIds",
      fieldType = ListType(Type),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = implicit ctx ⇒ Fetchers.networks.deferSeqOpt(idsArg.resolve),
      description = "Looks up versions with the given ids".some
    ),
    Field(
      "networks",
      ListType(Type),
      arguments = Nil, // TODO pagination
      resolve = implicit ctx ⇒ ctx.ctx.networks.getAll,
      description = "Fetches all versions".some
    )
  )

  override lazy val Type: ObjectType[Unit, Network] = ObjectType(
    name = "Network",
    description = "A collection of servers",
    fieldsFn = () ⇒
      idFields[Network, Long] ++ modificationTimesFields ++ fields[Unit, Network](
        Field(
          name = "name",
          fieldType = StringType,
          description = "The unique name of this network".some,
          resolve = _.value.name
        ),
        Field(
          name = "tag",
          fieldType = StringType,
          description = "The unique tag of this network".some,
          resolve = _.value.tag
        ),
        Field(
          name = "description",
          fieldType = StringType,
          description = "A markdown formatted description of this network".some,
          resolve = _.value.description
        ),
        Field(
          name = "deleted",
          fieldType = BooleanType,
          description = "Whether this item has been deleted or not".some,
          resolve = _.value.deleted
        ),
        // relations below here
        Field(
          name = "owner",
          fieldType = UserSchema.Type,
          description = "The owner of the network, has full control".some,
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.owner)
        ),
        Field(
          name = "servers", // TODO pagination
          fieldType = ListType(ServerSchema.Type),
          description = "All of the servers belonging to this network".some,
          resolve = ctx ⇒ Fetchers.servers.deferRelSeq(Relations.serverByNetworkId, ctx.value.id)
        ),
        Field(
          name = "permissions", // TODO pagination
          fieldType = ListType(NetworkPermissionSchema.Type),
          description = "A list of all users with permissions".some,
          resolve = ctx ⇒ Fetchers.networkPermissions.deferRelSeq(Relations.networkPermissionByNetworkId, ctx.value.id)
        )
    )
  )
}
