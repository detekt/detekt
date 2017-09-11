package cases

@Suppress("unused")
fun loopConditions() {
	for (i in 2..2) { }
	for (i in 2..1) { } // violation
	for (i in 2 downTo 2) { }
	for (i in 1 downTo 2) { } // violation
	for (i in 2 until 2) {
		for (j in 2 until 1) { } // violation
	}
}
