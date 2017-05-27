package gg.uhc.repositories

import java.util.UUID

import gg.uhc.website.repositories.UserRepository
import org.scalatest._

import scalaz.NonEmptyList

@DoNotDiscover
class UserRepositoryTest extends FunSuite with Matchers with BaseRepositoryTest {
  test("change password query") {
    check(UserRepository.changePasswordQuery(UUID.randomUUID(), "test"))
  }

  test("create user query") {
    check(UserRepository.createUserQuery("name", "email", "password"))
  }

  test("check username in use query") {
    check(UserRepository.checkUsernameInUseQuery("test"))
  }

  test("query by id") {
    check(UserRepository.getByIdQuery(UUID.randomUUID()))
  }

  test("query by multiple ids") {
    check(UserRepository.getByIdsQuery(NonEmptyList(UUID.randomUUID(), UUID.randomUUID())))
  }

  test("query by username") {
    check(UserRepository.getByUsernameQuery("test"))
  }

  test("query by multiple usernames") {
    check(UserRepository.getByUsernamesQuery(NonEmptyList("test", "names")))
  }

  test("query by login") {
    check(UserRepository.getByUsernameOrEmail("test"))
  }
}
