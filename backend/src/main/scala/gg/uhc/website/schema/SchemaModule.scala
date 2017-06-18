package gg.uhc.website.schema

import com.softwaremill.macwire._
import gg.uhc.website.repositories.RepositoriesModule
import gg.uhc.website.schema.types.objects.{MutationObjectType, QueryObjectType}
import gg.uhc.website.security.SecurityModule
import sangria.schema.Schema

trait SchemaModule extends RepositoriesModule with SecurityModule {
  val defaultMetadata = QueryMetadata(depth = None, complexity = None)

  val schema: Schema[SchemaContext, Unit] = Schema(
    query = QueryObjectType.Type,
    mutation = Some(MutationObjectType.Type)
  )

  lazy val schemaContextGenerator: () ⇒ SchemaContext = () ⇒ wire[SchemaContext]
}
