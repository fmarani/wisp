package org.flagzeta.wisp

import org.specs._
import org.flagzeta.wisp._

class EmailSpecs extends Specification {
	"user part is split correctly" in {
		(new Email("user@address.com")).user must_== "user"
	}
	"domain part is split correctly" in {
		(new Email("user@address.com")).domain must_== "address.com"
	}
	"acct address correct" in {
		(new Email("user@address.com")).acct must_== "acct:user@address.com"
	}
}

