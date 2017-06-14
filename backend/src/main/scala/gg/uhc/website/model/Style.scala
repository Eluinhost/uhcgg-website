package gg.uhc.website.model

import sangria.relay.Node

case class Style(id: String, shortName: String, fullName: String, description: String, requiresSize: Boolean)
    extends Node
