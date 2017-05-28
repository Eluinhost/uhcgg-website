package gg.uhc.website.model

case class Style(id: Int, shortName: String, fullName: String, description: String, requiresSize: Boolean)
    extends IdentificationFields[Int]
