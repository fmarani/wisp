package org.flagzeta.wisp.activity

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

	def apply(subject: ActivitySubject, activities: Seq[Activity]) = new ActivityStream(subject, activities)
}

