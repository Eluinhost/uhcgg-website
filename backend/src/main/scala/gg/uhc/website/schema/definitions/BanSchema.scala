package gg.uhc.website.schema.definitions

import gg.uhc.website.model.Ban
import sangria.schema._
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.SchemaIds._

import scalaz.Scalaz._

object BanSchema extends SchemaDefinition[Ban] with SchemaQueries with SchemaSupport {
  private val showExpiredArg = Argument(
    "showExpired",
    OptionInputType(BooleanType),
    description = "Set to true to show bans that have already expired",
    defaultValue = false
  )

  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      "bans",
      ListType(Type), // TODO pagination
      arguments = showExpiredArg :: Nil,
      resolve = implicit ctx ⇒ ctx.ctx.bans.getBansByExpiredStatus(showExpiredArg.resolve),
      description = "Look up current bans for the given user id".some
    )
  )

  override lazy val Type: ObjectType[Unit, Ban] = ObjectType(
    name = "Ban",
    description = "A ban on a particular user",
    fieldsFn = () ⇒
      idFields[Ban, Long] ++ modificationTimesFields ++ fields[Unit, Ban](
        Field(
          name = "reason",
          fieldType = StringType,
          description = "The reason this user is banned".some,
          resolve = _.value.reason
        ),
        Field(
          name = "expires",
          fieldType = DateType,
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
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.userId)
        ),
        Field(
          name = "author",
          fieldType = UserSchema.Type,
          description = "The user that created this ban".some,
          resolve = ctx ⇒ Fetchers.users.defer(ctx.value.author)
        )
    )
  )
}
