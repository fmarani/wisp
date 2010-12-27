package org.flagzeta.wisp

import scala.xml._
import org.specs._
import org.specs.util.DataTables

import org.flagzeta.wisp._

class ActivityStreamSpec extends Specification with DataTables {
	val identicaFeedExtract = <feed xml:lang="en-US" xmlns="http://www.w3.org/2005/Atom" xmlns:thr="http://purl.org/syndication/thread/1.0" xmlns:georss="http://www.georss.org/georss" xmlns:activity="http://activitystrea.ms/spec/1.0/" xmlns:media="http://purl.org/syndication/atommedia" xmlns:poco="http://portablecontacts.net/spec/1.0" xmlns:ostatus="http://ostatus.org/schema/1.0" xmlns:statusnet="http://status.net/schema/api/1/">
								 <generator uri="http://status.net" version="0.9.6">StatusNet</generator>
								 <id>http://identi.ca/api/statuses/user_timeline/18596.atom</id>
								 <title>X timeline</title>
								 <subtitle>Updates from X on Identi.ca!</subtitle>
								 <logo>http://avatar.identi.ca/18596-96-20090202192240.png</logo>
								 <updated>2010-12-19T18:53:40+00:00</updated>
								<author>
								 <name>marro</name>
								 <uri>http://identi.ca/user/18596</uri>
								</author>
								 <link href="http://identi.ca/marro" rel="alternate" type="text/html"/>
								 <link href="http://identi.ca/main/sup#18596" rel="http://api.friendfeed.com/2008/03#sup" type="application/json"/>
								 <link href="http://identi.ca/main/push/hub" rel="hub"/>
								 <link href="http://identi.ca/main/salmon/user/18596" rel="salmon"/>
								 <link href="http://identi.ca/main/salmon/user/18596" rel="http://salmon-protocol.org/ns/salmon-replies"/>
								 <link href="http://identi.ca/main/salmon/user/18596" rel="http://salmon-protocol.org/ns/salmon-mention"/>
								 <link href="http://identi.ca/api/statuses/user_timeline/18596.atom" rel="self" type="application/atom+xml"/>
								<activity:subject>
								 <activity:object-type>http://activitystrea.ms/schema/1.0/person</activity:object-type>
								 <id>http://identi.ca/user/18596</id>
								 <title>Federico Marani</title>
								 <link rel="alternate" type="text/html" href="http://identi.ca/marro"/>
								</activity:subject>
								<entry>
								 <title>gotta love these functional languages</title>
								 <link rel="alternate" type="text/html" href="http://identi.ca/notice/60539830"/>
								 <id>http://identi.ca/notice/60539830</id>
								 <published>2010-12-16T15:13:29+00:00</published>
								 <updated>2010-12-16T15:13:29+00:00</updated>
								 <activity:verb>http://activitystrea.ms/schema/1.0/post</activity:verb>
								 <activity:object-type>http://activitystrea.ms/schema/1.0/note</activity:object-type>
								 <statusnet:notice_info local_id="60539830" source="&lt;a href=&quot;http://launchpad.net/gwibber&quot; rel=&quot;nofollow&quot;&gt;Gwibber&lt;/a&gt;" source_link="http://launchpad.net/gwibber"></statusnet:notice_info>
								 <link rel="ostatus:conversation" href="http://identi.ca/conversation/59928426"/>
								</entry>
								<entry>
								 <title>struggling with time difference</title>
								 <link rel="alternate" type="text/html" href="http://identi.ca/notice/60526393"/>
								 <id>http://identi.ca/notice/60526393</id>
								 <published>2010-12-16T10:45:15+00:00</published>
								 <updated>2010-12-16T10:45:15+00:00</updated>
								 <activity:verb>http://activitystrea.ms/schema/1.0/post</activity:verb>
								 <activity:object-type>http://activitystrea.ms/schema/1.0/note</activity:object-type>
								 <statusnet:notice_info local_id="60526393" source="&lt;a href=&quot;http://launchpad.net/gwibber&quot; rel=&quot;nofollow&quot;&gt;Gwibber&lt;/a&gt;" source_link="http://launchpad.net/gwibber"></statusnet:notice_info>
								 <link rel="ostatus:conversation" href="http://identi.ca/conversation/59915389"/>
								</entry>
							</feed>
							
	val identicaExpResult = new ActivityStream(
		new Person("Federico Marani"),
		Seq(new Activity(Post, new Note("gotta love these functional languages"), new ActivityTarget("target")),
			new Activity(Post, new Note("struggling with time difference"), new ActivityTarget("target"))))

	"activitystream feed processor returns correct results for a set of samples" in {
		"input feeds"		| "results"		|>
		identicaFeedExtract	! identicaExpResult	| { (inxml: Elem, result: ActivityStream) =>
			val processor = new ActivityStreamFeedProcessor
			processor.processFeed(inxml) must_== result
		}
	}
}

