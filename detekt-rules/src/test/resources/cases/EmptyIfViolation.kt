package cases

@Suppress("unused")
class EmptyIfViolation {

	private var i = 0

	// if with trailing semicolon
	fun positive1() {
		if (i == 0)	;
		i++
	}

	// if with trailing semicolon
	fun positive2() {
		if (i == 0);
		i++
	}

	// if with semicolon on new line
	fun positive3() {
		if (i == 0)
			;
		i++
	}

	// if with semicolon and braces
	fun positive4() {
		if (i == 0) ; {
		}
	}
}
