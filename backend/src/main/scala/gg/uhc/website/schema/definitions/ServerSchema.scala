package gg.uhc.website.schema.definitions

import gg.uhc.website.model.Server
import sangria.schema._

import scalaz.Scalaz._

object ServerSchema extends SchemaDefinition[Server] with SchemaSupport {
  override lazy val Type: ObjectType[Unit, Server] = ObjectType(
    name = "Server",
    description = "A ban on a particular user",
    interfaces = interfaces[Unit, Server](RelaySchema.nodeInterface),
    fieldsFn = () ⇒
      idFields[Server] ++ modificationTimesFields ++ fields[Unit, Server](
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
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.owner)
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
          resolve = ctx ⇒ Fetchers.regions.defer(ctx.value.region)
        ),
        Field(
          name = "matches",
          fieldType = ListType(MatchSchema.Type), // TODO pagination + a way to filter out old ones
          description = "A list of games hosted on this server".some,
          resolve = ctx ⇒ Fetchers.matches.deferRelSeq(Relations.matchByServerId, ctx.value.id)
        )
    )
  )
}
