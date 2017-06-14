package gg.uhc.website.model

import sangria.relay.Node

case class Version(id: String, name: String, live: Boolean) extends Node
