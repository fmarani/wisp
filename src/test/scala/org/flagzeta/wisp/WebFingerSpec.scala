package org.flagzeta.wisp

import org.specs._
import org.flagzeta.wisp._

class WebFingerSpec extends Specification {
	class WebFingerMock extends WebFinger {
		override def getFingerXml(email: Email) = <XRD xmlns="http://docs.oasis-open.org/ns/xri/xrd-1.0">
	<Subject>acct:flagzeta@gmail.com</Subject>
	<Alias>http://www.google.com/profiles/flagzeta</Alias>
	<Link href="http://www-opensocial.googleusercontent.com/api/people/" rel="http://portablecontacts.net/spec/1.0"></Link>
	<Link href="http://www-opensocial.googleusercontent.com/api/people/105630410912031805918/" rel="http://portablecontacts.net/spec/1.0#me"></Link>
	<Link type="text/html" href="http://www.google.com/profiles/flagzeta" rel="http://webfinger.net/rel/profile-page"></Link>
	<Link type="text/html" href="http://www.google.com/profiles/flagzeta" rel="http://microformats.org/profile/hcard"></Link>
	<Link type="text/html" href="http://www.google.com/profiles/flagzeta" rel="http://gmpg.org/xfn/11"></Link>
	<Link href="http://www.google.com/profiles/flagzeta" rel="http://specs.openid.net/auth/2.0/provider"></Link>
	<Link type="text/html" href="http://www.google.com/profiles/flagzeta" rel="describedby"></Link>
	<Link type="application/rdf+xml" href="http://www.google.com/s2/webfinger/?q=acct%3Aflagzeta%40gmail.com&amp;fmt=foaf" rel="describedby"></Link>
	<Link type="application/atom+xml" href="https://www.googleapis.com/buzz/v1/activities/105630410912031805918/@public" rel="http://schemas.google.com/g/2010#updates-from"></Link>
</XRD>
	}

	val expectedResult = Map("http://gmpg.org/xfn/11" -> Map(), "http://schemas.google.com/g/2010#updates-from" -> Map(), "http://microformats.org/profile/hcard" -> Map(), "http://specs.openid.net/auth/2.0/provider" -> Map(), "http://portablecontacts.net/spec/1.0" -> Map(), "describedby" -> Map(), "http://webfinger.net/rel/profile-page" -> Map(), "http://portablecontacts.net/spec/1.0#me" -> Map())

	"webfinger return expected result from sample data" in {
		val wf = new WebFinger
		wf.finger(new Email("flagzeta@gmail.com")) must_== expectedResult
	}
}

