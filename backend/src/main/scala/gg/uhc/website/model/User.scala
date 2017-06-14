package gg.uhc.website.model

import java.time.Instant

import sangria.relay.Node

case class User(id: String, username: String, email: String, password: String, created: Instant, modified: Instant)
    extends Node
    with ModificationTimesFields
