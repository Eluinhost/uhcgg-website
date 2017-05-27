package gg.uhc.website.repositories

import java.util.UUID

import gg.uhc.website.schema.definitions.Relations
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class BanRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new BanRepository

  "BanRepository" should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(NonEmptyList(1, 2)))

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery(1L))

  it should "have valid getAllQuery query" in
    check(repo.getAllQuery)

  it should "have a valid getByExpiredStatusQuery query" in
    check(repo.getByExpiredStatusQuery(true))

  it should "have valid relationQuery query" in
    check(repo.relationsQuery(Relations.banByBannedUserId → Seq(UUID.randomUUID(), UUID.randomUUID())))
}
