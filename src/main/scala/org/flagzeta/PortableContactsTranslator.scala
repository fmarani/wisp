package org.flagzeta

import scala.util.parsing.json._

abstract class WebFingerTranslator {
	val NAMESPACES = List[String]()

	def apply(url: String): Map[String, Any]
}

object PortableContactsTranslator extends WebFingerTranslator {
	override val NAMESPACES = List("http://portablecontacts.net/spec/1.0#me")

	protected def retrieve(url: String) = io.Source.fromURL(url).mkString

	override def apply(url: String) = {
		val payload = retrieve(url)
		val untypedMap = JSON.parseFull(payload).getOrElse(Map()).asInstanceOf[Map[String,Any]]
		Map("name" -> untypedMap("displayName"))
	}
}


