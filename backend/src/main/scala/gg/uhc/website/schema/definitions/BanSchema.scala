package gg.uhc.website.schema.definitions

import gg.uhc.website.model.Ban
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.helpers.ConnectionIOConverters._
import gg.uhc.website.schema.helpers.ArgumentConverters._
import gg.uhc.website.schema.helpers.FieldHelpers._
import gg.uhc.website.schema.scalars.InstantScalarTypeSupport._

import sangria.schema._

import scalaz.Scalaz._

object BanSchema extends HasSchemaType[Ban] with HasSchemaQueries {
  private val showExpiredArg = Argument(
    "showExpired",
    OptionInputType(BooleanType),
    description = "Set to true to show bans that have already expired",
    defaultValue = false
  )

  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field( // TODO replace with a connection for pagination purposes
      "bans",
      ListType(Type),
      arguments = showExpiredArg :: Nil,
      resolve = implicit ctx ⇒ ctx.ctx.bans.getBansByExpiredStatus(showExpiredArg.resolve),
      description = "Look up current bans for the given user id".some
    )
  )

  override lazy val Type: ObjectType[SchemaContext, Ban] = ObjectType[SchemaContext, Ban](
    name = "Ban",
    description = "A ban on a particular user",
    interfaces = interfaces[SchemaContext, Ban](RelaySchema.nodeInterface),
    fieldsFn = () ⇒ fields[SchemaContext, Ban](
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
        fieldType = UserSchema.Type,
        description = "The user that this ban applies to".some,
        resolve = ctx ⇒ Fetchers.users.defer(ctx.value.bannedUserId)
      ),
      Field(
        name = "author",
        fieldType = UserSchema.Type,
        description = "The user that created this ban".some,
        resolve = ctx ⇒ Fetchers.users.defer(ctx.value.authorUserId)
      )
    )
  )
}
