package gg.uhc.website.schema.model

import java.time.Instant

trait ModificationTimesFields {
  val created: Instant
  val modified: Instant
}
