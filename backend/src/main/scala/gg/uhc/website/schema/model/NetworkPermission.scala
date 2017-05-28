package gg.uhc.website.schema.model

import java.util.UUID

case class NetworkPermission(networkId: Long, userId: UUID, isAdmin: Boolean)
