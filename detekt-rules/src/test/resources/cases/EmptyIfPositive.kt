package cases

@Suppress("unused")
class EmptyIfPositive {

	private var i = 0

	// reports 1
	fun trailingSemicolon1() {
		if (i == 0) ;
		i++
	}

	// reports 1
	fun trailingSemicolon2() {
		if (i == 0);
		i++
	}

	// reports 1
	fun semicolonOnNewLine() {
		if (i == 0)
			;
		i++
	}

	// reports 1
	fun semicolonAndBraces() {
		if (i == 0) ; {
		}
	}
}
