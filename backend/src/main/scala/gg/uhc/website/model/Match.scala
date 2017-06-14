package gg.uhc.website.model

import java.time.Instant

import sangria.relay.Node

case class Match(
    id: String,
    host: String,
    serverId: String,
    versionId: String,
    styleId: String,
    size: Option[Int],
    created: Instant,
    modified: Instant,
    deleted: Boolean,
    starts: Instant)
    extends Node
    with ModificationTimesFields
    with DeleteableFields
