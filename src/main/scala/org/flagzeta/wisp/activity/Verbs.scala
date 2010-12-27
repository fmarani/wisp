package org.flagzeta.wisp.activity

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

