package gg.uhc.website.model

import sangria.relay.Node

case class Role(id: String, name: String, permissions: List[String]) extends Node
