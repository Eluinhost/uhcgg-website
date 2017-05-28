package gg.uhc.website.model

import java.net.InetAddress
import java.time.Instant
import java.util.UUID

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
