package cases

@Suppress("unused")
class EmptyIfNegative {

	private var i = 0

	// normal if with braces
	fun negative1() {
		if (i == 0) {
			i++
		}
	}

	// normal if without braces
	fun negative2() {
		if (i == 0) i++
	}

	// if then with semicolon but nonempty else body
	fun negative3() {
		if (i == 0) ;
		else i++
	}

	// multiple if thens with semicolon but nonempty else body
	fun negative4() {
		if (i == 0) ;
		else if (i == 1) ;
		else i++
	}
}
