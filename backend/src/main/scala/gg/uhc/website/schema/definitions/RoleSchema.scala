package gg.uhc.website.schema.definitions

import gg.uhc.website.model.Role
import gg.uhc.website.schema.SchemaContext
import gg.uhc.website.schema.SchemaIds._
import sangria.schema._

import scalaz.Scalaz._

object RoleSchema extends SchemaDefinition[Role] with SchemaQueries with SchemaSupport {
  private val idArg  = Argument(name = "id", argumentType = IntType, description = "ID to match")
  private val idsArg = Argument(name = "ids", argumentType = ListInputType(IntType), description = "IDs to match")

  override val queries: List[Field[SchemaContext, Unit]] = fields(
    Field(
      name = "roleById",
      fieldType = OptionType(Type),
      arguments = idArg :: Nil,
      resolve = implicit ctx ⇒ Fetchers.roles.deferOpt(idArg.resolve),
      description = "Looks up a role with the given id".some
    ),
    Field(
      name = "rolesByIds",
      fieldType = ListType(Type),
      arguments = idsArg :: Nil,
      complexity = Some((_, args, childScore) ⇒ 20 + (args.arg(idsArg).length * childScore)),
      resolve = implicit ctx ⇒ Fetchers.roles.deferSeqOpt(idsArg.resolve),
      description = "Looks up roles with the given ids".some
    ),
    Field(
      "roles",
      ListType(Type), // TODO pagination
      arguments = Nil,
      resolve = implicit ctx ⇒ ctx.ctx.roles.getAll,
      description = "Fetches all roles".some
    )
  )

  override lazy val Type: ObjectType[Unit, Role] = ObjectType(
    name = "Role",
    description = "A website role to grant permission to a user",
    fieldsFn = () ⇒
      idFields[Role, Int] ++ fields[Unit, Role](
        Field(
          name = "name",
          fieldType = StringType,
          description = "The unqiue name of this role".some,
          resolve = _.value.name
        ),
        Field(
          name = "permissions",
          fieldType = ListType(StringType),
          description = "The granted permissions for users with this role".some,
          resolve = _.value.permissions
        ),
        //////////////////////////
        // Relations below here //
        //////////////////////////
        Field(
          name = "users",
          fieldType = ListType(UserRoleSchema.Type),
          description = "List of UserRole objects to get users with this role".some,
          resolve = ctx ⇒ Fetchers.userRoles.deferRelSeq(Relations.userRoleByRoleId, ctx.value.id)
        )
    )
  )
}
