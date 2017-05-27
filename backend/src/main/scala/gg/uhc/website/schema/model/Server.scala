package gg.uhc.website.schema.model

import java.net.InetAddress
import java.time.Instant
import java.util.UUID

import sangria.execution.deferred.HasId
import sangria.macros.derive.{GraphQLDescription, GraphQLName}

object Server {
  implicit val hasId: HasId[Server, Long] = HasId(_.id)
}

@GraphQLName("Server")
@GraphQLDescription("A hostable server, part of a single network")
case class Server(
    @GraphQLDescription("The unique ID of this server") id: Long,
    owner: UUID,
    networkId: Long,
    @GraphQLDescription("The name of this server, must be unique within the network") name: String,
    @GraphQLDescription("Optional address to connect to the server") address: Option[String],
    @GraphQLDescription("The direct connect IP address of the server") ip: InetAddress,
    @GraphQLDescription("Optional port to use to connect") port: Option[Int],
    @GraphQLDescription("Text description of where about the server is hosted") location: String,
    region: Int,
    @GraphQLDescription("When the server was first created") created: Instant,
    @GraphQLDescription("When the server was last modified") modified: Instant,
    @GraphQLDescription("Whether the server has been deleted or not") deleted: Boolean)
    extends IdentifiableModel[Long]