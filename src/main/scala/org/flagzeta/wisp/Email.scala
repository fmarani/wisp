package org.flagzeta.wisp

class Email(val address: String) {
	val user = address.split("@")(0)
	val domain = address.split("@")(1)
	val acct = "acct:" + address
}

