package gg.uhc.website.model

import java.util.UUID

case class Role(uuid: UUID, name: String, permissions: List[String]) extends BaseNode
