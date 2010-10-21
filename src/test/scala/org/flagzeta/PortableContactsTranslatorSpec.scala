package org.flagzeta

import org.specs._
import org.flagzeta._

class PortableContactsTranslatorSpecs extends Specification {
	class PortableContactsTranslatorMock extends PortableContactsTranslator {
		override def retrieve(url: String) = """{"entry":{"profileUrl":"http://www.google.com/profiles/flagzeta","id":"105630410912031805918","name":{"formatted":"Federico Marani","familyName":"Marani","givenName":"Federico"},"urls":[{"value":"http://www.google.com/profiles/flagzeta","type":"profile"}],"displayName":"Federico Marani"}}"""
	}

	"portable contacts translator returns right displayName" in {
		val translator = new PortableContactsTranslatorMock
		translator("") must_== Map("name" -> "Federico Marani")
	}
}

