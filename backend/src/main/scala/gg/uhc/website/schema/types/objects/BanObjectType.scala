package gg.uhc.website.schema.types.objects

import gg.uhc.website.model.Ban
import gg.uhc.website.schema.helpers.FieldHelpers._
import gg.uhc.website.schema.types.scalars.InstantScalarType._
import gg.uhc.website.schema.{Fetchers, SchemaContext}
import sangria.schema._

import scalaz.Scalaz._

object BanObjectType extends HasObjectType[Ban] {
  override lazy val Type: ObjectType[SchemaContext, Ban] = ObjectType[SchemaContext, Ban](
    name = "Ban",
    description = "A ban on a particular user",
    interfaces = interfaces[SchemaContext, Ban](RelayDefinitions.nodeInterface),
    fieldsFn = () ⇒
      fields[SchemaContext, Ban](
        globalIdField,
        rawIdField,
        modifiedField,
        createdField,
        Field(
          name = "reason",
          fieldType = StringType,
          description = "The reason this user is banned".some,
          resolve = _.value.reason
        ),
        Field(
          name = "expires",
          fieldType = InstantType,
          description = "When this ban will end".some,
          resolve = _.value.expires
        ),
        //////////////////////////
        // Relations below here //
        //////////////////////////
        Field(
          name = "user",
          fieldType = UserObjectType.Type,
          description = "The user that this ban applies to".some,
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.bannedUserId)
        ),
        Field(
          name = "author",
          fieldType = UserObjectType.Type,
          description = "The user that created this ban".some,
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.authorUserId)
        )
    )
  )
}
