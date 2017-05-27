package gg.uhc.website.repositories

import java.util.UUID

import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class UserRepositoryTest extends FlatSpec with BaseRepositoryTest {
  val repo = new UserRepository

  "UserRepository" should "have valid changePasswordQuery query" in
    check(repo.changePasswordQuery(UUID.randomUUID(), "test"))

  it should "have valid createUserQuery query" in
    check(repo.createUserQuery("name", "email", "password"))

  it should "have valid checkUsernameInUseQuery query" in
    check(repo.checkUsernameInUseQuery("test"))

  it should "have valid getByIdQuery query" in
    check(repo.getByIdQuery(UUID.randomUUID()))

  it should "have valid getByIdsQuery query" in
    check(repo.getByIdsQuery(NonEmptyList(UUID.randomUUID(), UUID.randomUUID())))

  it should "have valid getByUsernameQuery query" in
    check(repo.getByUsernameQuery("test"))

  it should "have valid getByUsernamesQuery query" in
    check(repo.getByUsernamesQuery(NonEmptyList("test", "names")))

  it should "have valid getByUsernameOrEmail query" in
    check(repo.getByUsernameOrEmailQuery("test"))
}
