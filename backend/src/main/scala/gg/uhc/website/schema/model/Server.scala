package gg.uhc.website.schema.model

import java.net.InetAddress
import java.time.Instant
import java.util.UUID

import sangria.execution.deferred.HasId

object Server {
  implicit val hasId: HasId[Server, Long] = HasId(_.id)
}

case class Server(
    id: Long,
    owner: UUID,
    networkId: Long,
    name: String,
    address: Option[String],
    ip: InetAddress,
    port: Option[Int],
    location: String,
    region: Int,
    created: Instant,
    modified: Instant,
    deleted: Boolean)
    extends IdentificationFields[Long]
    with ModificationTimesFields
    with DeleteableFields
