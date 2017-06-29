package gg.uhc.website.repositories

trait ListingParameters[Cursor] {
  val after: Option[Cursor]
  val count: Int

  def withRelId[RelId](relId: RelId) =
    RelationshipListingParameters(after, count, relId)
}

case class DefaultListingParameters[Cursor](after: Option[Cursor], count: Int) extends ListingParameters[Cursor]

case class RelationshipListingParameters[RelId, Cursor](after: Option[Cursor], count: Int, relId: RelId)
    extends ListingParameters[Cursor]
