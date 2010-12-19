import sbt._ 
 
class WispProject(info: ProjectInfo) extends DefaultProject(info) { 
	val scalaToolsSnapshots = ScalaToolsSnapshots
	val specs = "org.scala-tools.testing" % "specs_2.8.0" % "1.6.5" % "test"

	val restletRepo = "restlet repository" at "http://maven.restlet.org"

	val restlet = "com.noelios.restlet" % "com.noelios.restlet" % "1.1.6"
	val restletHttpClient = "com.noelios.restlet" % "com.noelios.restlet.ext.httpclient" % "1.1.6"
	val restletSimple = "com.noelios.restlet" % "com.noelios.restlet.ext.simple" % "1.1.6"
}
