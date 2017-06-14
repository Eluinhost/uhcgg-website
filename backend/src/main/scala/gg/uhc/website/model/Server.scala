package gg.uhc.website.model

import java.net.InetAddress
import java.time.Instant

import sangria.relay.Node

case class Server(
    id: String,
    owner: String,
    networkId: String,
    name: String,
    address: Option[String],
    ip: InetAddress,
    port: Option[Int],
    location: String,
    region: String,
    created: Instant,
    modified: Instant,
    deleted: Boolean)
    extends Node
    with ModificationTimesFields
    with DeleteableFields
