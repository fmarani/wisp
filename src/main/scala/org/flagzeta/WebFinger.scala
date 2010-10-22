package org.flagzeta

import java.net._
import scala.xml._
import scala.collection.mutable.ListBuffer
import org.flagzeta._

class WebFinger {
	private val translators = new ListBuffer[WebFingerTranslator]()

	def addTranslator(wft: WebFingerTranslator) = {
		this.translators += wft	
	}

	protected def getFingerXml(email: Email) = {
		val hostMeta = XML.load(new URL("http://" + email.domain + "/.well-known/host-meta"))
		val lrdds = (hostMeta \\ "Link") filter (link => (link \ "@rel").text == "lrdd")
		val fingerServiceUrl = ((lrdds(0) \ "@template").text).replace("{uri}", email.acct)
		XML.load(fingerServiceUrl)
	}

	def listServices(email: Email) = (this.getFingerXml(email) \\ "Link") map (link => Map((link \ "@rel").text -> (link \ "@href").text) ) reduceLeft ((a, b) => a ++ b)

	def translateServices(email: Email) = {
		listServices(email).transform( (namespace, pointer) => {
			val requiredTranslator = this.translators.find(_.NAMESPACES contains namespace)
			requiredTranslator match {
				case Some(t: WebFingerTranslator) => {
					t(pointer)
				}
				case None => Map[String, Any]()
			}
		})
	}

	def finger = translateServices _

}

object WebFingerMain {
	def main(args: Array[String]) {
		val wf = new WebFinger
		//wf.addTranslator(new PortableContactsTranslator)
		val services = wf.translateServices(new Email("flagzeta@gmail.com"))
		println(services)
	}
}
