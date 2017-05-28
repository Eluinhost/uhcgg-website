package gg.uhc.website.model

import java.util.UUID

case class NetworkPermission(networkId: Long, userId: UUID, isAdmin: Boolean)
