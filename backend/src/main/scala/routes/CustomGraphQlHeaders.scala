package routes

import akka.http.scaladsl.model.headers.{ModeledCustomHeader, ModeledCustomHeaderCompanion}

import scala.util.Try

final case class GraphQlComplexityHeader(complexity: Double) extends ModeledCustomHeader[GraphQlComplexityHeader] {
  override def renderInRequests(): Boolean = false
  override def renderInResponses(): Boolean = true
  override val companion = GraphQlComplexityHeader
  override def value(): String = complexity.toString
}
object GraphQlComplexityHeader extends ModeledCustomHeaderCompanion[GraphQlComplexityHeader] {
  override val name = "GraphQL-Complexity"
  override def parse(value: String) = Try { new GraphQlComplexityHeader(value.toDouble) }
}

final case class GraphQlDepthHeader(complexity: Int) extends ModeledCustomHeader[GraphQlDepthHeader] {
  override def renderInRequests(): Boolean = false
  override def renderInResponses(): Boolean = true
  override val companion = GraphQlDepthHeader
  override def value(): String = complexity.toString
}
object GraphQlDepthHeader extends ModeledCustomHeaderCompanion[GraphQlDepthHeader] {
  override val name = "GraphQL-Depth"
  override def parse(value: String) = Try { new GraphQlDepthHeader(value.toInt) }
}