@file:Suppress("unused")

/**
 * @author Artur Bosch
 */

@SuppressWarnings("LongParameterList")
fun lpl(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) = (a + b + c + d + e + f).apply {
	assert(false) { "FAILED TEST" }
}

@SuppressWarnings("ComplexCondition")
class SuppressedElements {

	@SuppressWarnings("LongParameterList")
	fun lpl(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) = (a + b + c + d + e + f).apply {
		assert(false) { "FAILED TEST" }
	}

	@SuppressWarnings("ComplexCondition")
	fun cc() {
		if (this is SuppressedElements && this !is Any && this is Nothing && this is SuppressedElements) {
			assert(false) { "FAIL" }
		}
	}

	@SuppressWarnings("LongMethod")
	fun lm() {
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		lpl(1, 2, 3, 4, 5, 6)
		assert(false) { "FAILED TEST" }
	}

	@SuppressWarnings("MaxLineLength")
	fun lineLength() {
		val s = "Lorem ipsum dolor sit amet, wisi nominavi usu ne. Sea in impedit patrioque, vis cu moderatius quaerendum scribentur. Ex cum appareat ocurreret delicatissimi. Usu harum labores te. Natum signiferumque no nam, est id oratio blandit. Temporibus consectetuer consequuntur ei est, his in dolorum vituperata."
	}

}