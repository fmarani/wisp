package org.flagzeta.wisp.activity

sealed abstract class ActivityObject(val title: String)
case class Note(override val title: String) extends ActivityObject(title)
object Note {
	val ns = "http://activitystrea.ms/schema/1.0/note"
}
