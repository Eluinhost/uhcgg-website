package gg.uhc.website.model

import java.net.InetAddress
import java.time.Instant
import java.util.UUID

case class Server(
    uuid: UUID,
    ownerUserId: UUID,
    networkId: UUID,
    name: String,
    address: Option[String],
    ip: InetAddress,
    port: Option[Int],
    location: String,
    regionId: UUID,
    created: Instant,
    modified: Instant,
    deleted: Boolean)
    extends BaseNode
    with ModificationTimesFields
    with DeleteableFields
