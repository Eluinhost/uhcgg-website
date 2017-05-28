package gg.uhc.website.model

case class Role(id: Int, name: String, permissions: List[String]) extends IdentificationFields[Int]
