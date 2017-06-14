package gg.uhc.website.model

import java.util.UUID

case class Style(uuid: UUID, shortName: String, fullName: String, description: String, requiresSize: Boolean)
    extends BaseNode
