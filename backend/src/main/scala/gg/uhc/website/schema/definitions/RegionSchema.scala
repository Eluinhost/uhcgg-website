package gg.uhc.website.schema.definitions

import java.util.UUID

import gg.uhc.website.model.{Region, Server}
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.helpers.ConnectionHelpers._
import gg.uhc.website.schema.helpers.ConnectionIOConverters._
import gg.uhc.website.schema.helpers.FieldHelpers._
import gg.uhc.website.schema.scalars.UuidScalarTypeSupport._

import sangria.schema._
import scalaz.Scalaz._

object RegionSchema extends HasSchemaType[Region] with HasSchemaQueries {
  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      "regions",
      ListType(Type),
      arguments = Nil, // TODO replace with a connection for pagination purposes
      resolve = implicit ctx ⇒ ctx.ctx.regions.getAll,
      description = "Fetches all regions".some
    )
  )

  override lazy val Type: ObjectType[SchemaContext, Region] = ObjectType[SchemaContext, Region](
    name = "Region",
    description = "A choosable region for hosting in",
    interfaces = interfaces[SchemaContext, Region](RelaySchema.nodeInterface),
    fieldsFn = () ⇒
      fields[SchemaContext, Region](
        globalIdField,
        rawIdField,
        Field(
          name = "short",
          fieldType = StringType,
          description = "The 'short' verison of the name".some,
          resolve = _.value.short
        ),
        Field(
          name = "long",
          fieldType = StringType,
          description = "The 'full' version of the name".some,
          resolve = _.value.long
        ),
        // Connections below here
        relationshipField[Region, Server, UUID, UUID](
          name = "servers",
          targetType = ServerSchema.Type,
          description = "A list of servers in this region",
          action = _.servers.getByRegionId,
          cursorFn = (s: Server) ⇒ s.uuid,
          idFn = (r: Region) ⇒ r.uuid
        )
    )
  )
}
