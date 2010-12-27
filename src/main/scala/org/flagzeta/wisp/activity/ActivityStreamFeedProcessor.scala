package org.flagzeta.wisp.activity

import scala.xml._

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
