package gg.uhc.website.repositories

import java.util.UUID

import scoobie.doobie.ScoobieFragmentProducer
import doobie.postgres.imports._

trait HasUuidIdColumn[A] extends HasIdColumn[A, UUID] { self: Repository[A] ⇒
  override private[repositories] val idFragmentProducer: ScoobieFragmentProducer[UUID] =
    implicitly[ScoobieFragmentProducer[UUID]]
}

