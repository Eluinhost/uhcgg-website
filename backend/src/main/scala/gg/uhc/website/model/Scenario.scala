package gg.uhc.website.model

import java.time.Instant

import sangria.relay.Node

case class Scenario(
    id: String,
    name: String,
    description: String,
    created: Instant,
    modified: Instant,
    deleted: Boolean,
    owner: String)
    extends Node
    with ModificationTimesFields
    with DeleteableFields
