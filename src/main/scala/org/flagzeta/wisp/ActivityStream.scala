package org.flagzeta.wisp

import scala.xml._

sealed abstract class ActivitySubject(val displayName: String)
case class Person(override val displayName: String) extends ActivitySubject(displayName)
object Person {
	val ns = "http://activitystrea.ms/schema/1.0/person"
}

sealed abstract class ActivityVerb(val displayName: String)
case object Post extends ActivityVerb("posted") {
	val ns = "http://activitystrea.ms/schema/1.0/post"
}

case object Tag extends ActivityVerb("tagged") {
	val ns = "http://activitystrea.ms/schema/1.0/tag"
}

case object Commit extends ActivityVerb("committed") {
	val ns = "http://activitystrea.ms/schema/1.0/commit"
}

case object Meet extends ActivityVerb("met") {
	val ns = "http://activitystrea.ms/schema/1.0/meet"
}


sealed abstract class ActivityObject(val title: String)
case class Note(override val title: String) extends ActivityObject(title)
object Note {
	val ns = "http://activitystrea.ms/schema/1.0/note"
}

case class ActivityTarget(val displayName: String)

case class Activity(
	val Verb: ActivityVerb,
	val Object: ActivityObject,
	val Target: ActivityTarget
)

class ActivityStream(val subject: ActivitySubject, val activities: Seq[Activity]) extends Seq[Activity] {
	def apply(i: Int) = activities(i)
	def length = activities.length
	def iterator = activities.toIterator
}

object ActivityStream {
	val ns = "http://activitystrea.ms/spec/1.0/"
}

class ActivityStreamFeedProcessor {
	def p = "@{" + ActivityStream.ns + "}" + _
	
	def processFeed(atom: Elem) = {
		val activityStream = (atom \ "entry") map (x => processEntry(x))
		val subjectTag = atom \ p("subject")
		val subject = (subjectTag \ p("object-type")).text match {
			case Person.ns => Some(new Person((subjectTag \ "title").text))
			case _ => None
		}
		new ActivityStream(subject.getOrElse(new Person("")), activityStream)
	}
	
	def processEntry(entry: Node) = {
		val _verb = (entry \ p("verb")).text match {
			case Post.ns => Some(Post)
			case Tag.ns => Some(Tag)
			case Commit.ns => Some(Commit)
			case Meet.ns => Some(Meet)
			case _ => None
		}
		val _object = (entry \ p("object-type")).text match {
			case Note.ns => Some(new Note((entry \ "title").text))
			case _ => None
		}
		val _target = new ActivityTarget("target")
		new Activity(
			_verb.getOrElse(Post),
			_object.getOrElse(new Note((entry \ "title").text)),
			_target
		)
	}
}
