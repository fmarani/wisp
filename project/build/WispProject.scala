import sbt._ 
 
class WispProject(info: ProjectInfo) extends DefaultProject(info) { 
	val scalaToolsSnapshots = ScalaToolsSnapshots
	val specs = "org.scala-tools.testing" % "specs_2.8.0" % "1.6.5" % "test"
}
