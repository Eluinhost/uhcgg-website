package gg.uhc.website.repositories

import java.time.Instant
import java.util.UUID

import gg.uhc.website.model.Match
import scoobie.doobie.ScoobieFragmentProducer

class MatchRepository extends Repository[Match] with HasUuidIdColumn[Match] with HasRelationColumns[Match] {
  import scoobie.ast._
  import scoobie.doobie.doo.postgres._
  import scoobie.snacks.mild.sql._
  import doobie.imports._
  import doobie.postgres.imports._

  override private[repositories] val composite: Composite[Match] = implicitly[Composite[Match]]

  override private[repositories] val baseSelect =
    select(
      p"uuid",
      p"host_user_id",
      p"server_id",
      p"version_id",
      p"style_id",
      p"size",
      p"created",
      p"modified",
      p"deleted",
      p"starts"
    ) from p"matches"

  override private[repositories] val idColumn = p"uuid"

  private[this] val genericRelationListing = (column: QueryPath[ScoobieFragmentProducer]) ⇒
    relationListingQuery[UUID, Instant](
      relColumn = column,
      sort = p"created".desc
    )(_)

  private[repositories] val getByServerIdQuery   = genericRelationListing(p"server_id")
  private[repositories] val getByHostUserIdQuery = genericRelationListing(p"host_user_id")
  private[repositories] val getByStyleIdQuery    = genericRelationListing(p"style_id")
  private[repositories] val getByVersionIdQuery  = genericRelationListing(p"version_id")

  private[repositories] val getUpcomingMatchesQuery = relationListingQuery[Boolean, Instant](
    relColumn = p"deleted",
    sort = p"starts".desc
  )(_)

  def getByServerId(params: RelationshipListingParameters[UUID, Instant]): ConnectionIO[List[Match]] =
    getByServerIdQuery(params).list

  def getByHostUserId(params: RelationshipListingParameters[UUID, Instant]): ConnectionIO[List[Match]] =
    getByHostUserIdQuery(params).list

  def getByStyleId(params: RelationshipListingParameters[UUID, Instant]): ConnectionIO[List[Match]] =
    getByStyleIdQuery(params).list

  def getByVersionId(params: RelationshipListingParameters[UUID, Instant]): ConnectionIO[List[Match]] =
    getByVersionIdQuery(params).list

  def getUpcomingMatches(params: RelationshipListingParameters[Boolean, Instant]): ConnectionIO[List[Match]] =
    getUpcomingMatchesQuery(params).list
}
