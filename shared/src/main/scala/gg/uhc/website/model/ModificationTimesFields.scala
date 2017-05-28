package gg.uhc.website.model

import java.time.Instant

trait ModificationTimesFields {
  val created: Instant
  val modified: Instant
}
