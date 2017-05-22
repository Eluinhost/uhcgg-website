package gg.uhc.website.schema

import com.softwaremill.macwire._
import gg.uhc.website.repositories.RepositoriesModule

trait SchemaModule extends RepositoriesModule {
  val defaultMetadata = QueryMetadata(depth = None, complexity = None)

  lazy val schemaContextGenerator: () ⇒ SchemaContext = () ⇒ wire[SchemaContext]
}
