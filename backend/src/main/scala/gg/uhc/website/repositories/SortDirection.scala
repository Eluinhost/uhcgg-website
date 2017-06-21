package gg.uhc.website.repositories

object SortDirection {
  case object ASC  extends SortDirection
  case object DESC extends SortDirection
}

sealed trait SortDirection