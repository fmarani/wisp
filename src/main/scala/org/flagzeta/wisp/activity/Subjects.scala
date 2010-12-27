package org.flagzeta.wisp.activity

sealed abstract class ActivitySubject(val displayName: String)
case class Person(override val displayName: String) extends ActivitySubject(displayName)
object Person {
	val ns = "http://activitystrea.ms/schema/1.0/person"
}

