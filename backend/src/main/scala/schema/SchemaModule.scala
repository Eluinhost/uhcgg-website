package schema

import com.softwaremill.macwire.wire
import repositories.RepositoriesModule

trait SchemaModule extends RepositoriesModule {
  lazy val schemaContext: SchemaContext = wire[SchemaContext]
}
