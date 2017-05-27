package gg.uhc.repositories

import java.util.UUID

import gg.uhc.website.repositories.UserRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class UserRepositoryTest extends FlatSpec with BaseRepositoryTest {
  "UserRepository" should "have valid changePasswordQuery query" in
    check(UserRepository.changePasswordQuery(UUID.randomUUID(), "test"))

  it should "have valid createUserQuery query" in
    check(UserRepository.createUserQuery("name", "email", "password"))

  it should "have valid checkUsernameInUseQuery query" in
    check(UserRepository.checkUsernameInUseQuery("test"))

  it should "have valid getByIdQuery query" in
    check(UserRepository.getByIdQuery(UUID.randomUUID()))

  it should "have valid getByIdsQuery query" in
    check(UserRepository.getByIdsQuery(NonEmptyList(UUID.randomUUID(), UUID.randomUUID())))

  it should "have valid getByUsernameQuery query" in
    check(UserRepository.getByUsernameQuery("test"))

  it should "have valid getByUsernamesQuery query" in
    check(UserRepository.getByUsernamesQuery(NonEmptyList("test", "names")))

  it should "have valid getByUsernameOrEmail query" in
    check(UserRepository.getByUsernameOrEmail("test"))
}
