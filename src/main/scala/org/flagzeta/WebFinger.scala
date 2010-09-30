package org.flagzeta

import java.net._
import scala.xml._
import scala.collection.mutable.ListBuffer

class WebFinger(private val email: Email) {
	private val translators = new ListBuffer[WebFingerTranslator]()

	def addTranslator(wft: WebFingerTranslator) = {
		this.translators += wft	
	}

	protected def getFingerXml = {
		val hostMeta = XML.load(new URL("http://" + email.domain + "/.well-known/host-meta"))
		val lrdds = (hostMeta \\ "Link") filter (link => (link \ "@rel").text == "lrdd")
		val fingerServiceUrl = ((lrdds(0) \ "@template").text).replace("{uri}", email.acct)
		XML.load(fingerServiceUrl)
	}

	def listServices = (this.getFingerXml \\ "Link") map (link => Map((link \ "@rel").text -> (link \ "@href").text) ) reduceLeft ((a, b) => a ++ b)

	def translateServices = {
		listServices.transform( (namespace, pointer) => {
			println(namespace)
			val requiredTranslator = this.translators.find(_.NAMESPACES contains namespace)
			requiredTranslator match {
				case Some(t: WebFingerTranslator) => {
					println("applying " + t.toString)
					t(pointer)
				}
				case None => Map[String, Any]()
			}
		})
	}


}

object WebFingerMain {
	def main(args: Array[String]) {
		val wf = new WebFinger(new Email("flagzeta@gmail.com"))
		val services = wf.translateServices
		services foreach (println(_))
	}
}
