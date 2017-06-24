package gg.uhc.website.repositories

import java.util.UUID

import doobie.scalatest.QueryChecker
import doobie.util.iolite.IOLite
import doobie.util.transactor.{DataSourceTransactor, Transactor}
import gg.uhc.website.configuration.ConfigurationModule
import org.scalatest.{Assertions, FlatSpec, Matchers}

import scalaz.NonEmptyList

abstract class BaseRepositoryTest[T <: Repository[_]]
    extends FlatSpec
    with Matchers
    with QueryChecker
    with Assertions
    with ConfigurationModule
    with HasDataSource {

  val repo: T

  def randId: UUID                = UUID.randomUUID()
  def randIds: NonEmptyList[UUID] = NonEmptyList(randId, randId)
  def randIdsSeq: Seq[UUID]       = randId :: randId :: Nil

  override val transactor: Transactor[IOLite] = DataSourceTransactor[IOLite](dataSource)
}
