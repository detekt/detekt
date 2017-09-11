package cases

@Suppress("unused")
class LoopWithTooManyJumpStatements {

	fun tooManyJumps() { // reports 3
		val i = 0
		for (j in 1..2) {
			if (i > 1) {
				break
			} else {
				continue
			}
		}
		while (i < 2) {
			if (i > 1) break else continue
		}
		do {
			if (i > 1) break else continue
		} while (i < 2)
	}

	fun onlyOneJump() {
		for (i in 1..2) {
			if (i > 1) break
		}
	}
}
