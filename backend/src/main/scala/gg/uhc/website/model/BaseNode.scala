package gg.uhc.website.model

import java.util.UUID

import sangria.relay.Node

trait BaseNode extends Node {
  val uuid: UUID

  override def id: String = uuid.toString
}
