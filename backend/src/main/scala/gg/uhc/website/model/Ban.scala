package gg.uhc.website.model

import java.time.Instant

import sangria.relay.Node

case class Ban(
    id: String,
    reason: String,
    created: Instant,
    modified: Instant,
    expires: Instant,
    userId: String,
    author: String)
    extends Node
    with ModificationTimesFields
