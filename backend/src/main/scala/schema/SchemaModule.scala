package schema

import com.softwaremill.macwire._
import repositories.RepositoriesModule

trait SchemaModule extends RepositoriesModule {
  lazy val schemaContextGenerator: () ⇒ SchemaContext = () ⇒ wire[SchemaContext]
}
