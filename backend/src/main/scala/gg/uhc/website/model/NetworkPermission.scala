package gg.uhc.website.model

import java.util.UUID

case class NetworkPermission(networkId: UUID, userId: UUID, isAdmin: Boolean)
