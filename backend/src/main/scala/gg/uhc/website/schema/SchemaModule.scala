package gg.uhc.website.schema

import com.softwaremill.macwire._
import gg.uhc.website.repositories.RepositoriesModule
import gg.uhc.website.security.SecurityModule

trait SchemaModule extends RepositoriesModule with SecurityModule {
  val defaultMetadata = QueryMetadata(depth = None, complexity = None)

  lazy val schemaContextGenerator: () ⇒ SchemaContext = () ⇒ wire[SchemaContext]
}
