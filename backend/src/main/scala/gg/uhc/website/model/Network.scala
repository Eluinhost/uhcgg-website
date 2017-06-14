package gg.uhc.website.model

import java.time.Instant

import sangria.relay.Node

case class Network(
    id: String,
    name: String,
    tag: String,
    description: String,
    created: Instant,
    modified: Instant,
    deleted: Boolean,
    owner: String)
    extends Node
    with ModificationTimesFields
    with DeleteableFields
