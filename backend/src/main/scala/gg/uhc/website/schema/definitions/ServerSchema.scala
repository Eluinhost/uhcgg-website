package gg.uhc.website.schema.definitions

import gg.uhc.website.model.{Match, Server}
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.ConnectionIOConverters._
import gg.uhc.website.schema.scalars.InetAddressScalarTypeSupport._

import sangria.schema._

import scalaz.Scalaz._
import gg.uhc.website.schema.helpers.FieldHelpers._

object ServerSchema extends HasSchemaType[Server] with HasSchemaQueries {
  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      "servers",
      ListType(Type),
      arguments = Nil,// TODO replace with a connection for pagination purposes
      resolve = implicit ctx ⇒ ctx.ctx.servers.getAll,
      description = "Fetches all servers".some
    )
  )

  override lazy val Type: ObjectType[SchemaContext, Server] = ObjectType[SchemaContext, Server](
    name = "Server",
    description = "A server that can be hosted on, must belong to a network",
    interfaces = interfaces[SchemaContext, Server](RelaySchema.nodeInterface),
    fieldsFn = () ⇒
      fields[SchemaContext, Server](
        globalIdField,
        rawIdField,
        createdField,
        modifiedField,
        deletedField,
        Field(
          name = "address",
          fieldType = OptionType(StringType),
          description = "Optional address to connect to the server".some,
          resolve = _.value.address
        ),
        Field(
          name = "ip",
          fieldType = InetType,
          description = "The direct connect IP address of the server".some,
          resolve = _.value.ip
        ),
        Field(
          name = "port",
          fieldType = OptionType(IntType),
          description = "Optional port to use to connect".some,
          resolve = _.value.port
        ),
        Field(
          name = "location",
          fieldType = StringType,
          description = "Text description of where about the server is hosted".some,
          resolve = _.value.location
        ),
        Field(
          name = "name",
          fieldType = StringType,
          description = "The unique name (per-network) of this server".some,
          resolve = _.value.name
        ),
        //////////////////////////
        // Relations below here //
        //////////////////////////
        Field(
          name = "owner",
          fieldType = UserSchema.Type,
          description = "The owner of this server, has control over this server".some,
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.ownerUserId)
        ),
        Field(
          name = "network",
          fieldType = NetworkSchema.Type,
          description = "The network this server belongs to".some,
          resolve = ctx ⇒ Fetchers.networks.defer(ctx.value.networkId)
        ),
        Field(
          name = "region",
          fieldType = RegionSchema.Type,
          description = "The region the server is hosted in".some,
          resolve = ctx ⇒ Fetchers.regions.defer(ctx.value.regionId)
        ),
        // Connections below here
        simpleConnectionField[Server, Match](
          name = "matches",
          target = MatchSchema.Type,
          description = "A list of games hosted on this server",
          action = _.matches.getByServerId,
          cursorFn = _.created.toString
        )
    )
  )
}
