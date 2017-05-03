package schema.context

import schema.RoleSchemaDefinition.Role

import scala.concurrent.Future

/**
  * Created by Graham on 2017-05-05.
  */
trait RoleContext {
  def getRoles: Future[List[Role]]
}
