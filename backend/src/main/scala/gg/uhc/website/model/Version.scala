package gg.uhc.website.model

import java.util.UUID

case class Version(uuid: UUID, name: String, live: Boolean) extends BaseNode