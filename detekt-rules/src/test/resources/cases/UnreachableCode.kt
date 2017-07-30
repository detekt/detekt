package cases

@Suppress("unused", "UNREACHABLE_CODE")
class UnreachableCode {

	fun return1(p: Int) {
		if (p == 0) {
			return
			println()
		}
	}

	fun return2(p: String) : Boolean {
		p.let {
			return it.length < 3
			println()
		}
		return false
	}

	fun throw1(p: Int): Int {
		if (p == 0) {
			throw IllegalArgumentException()
			println()
		}
		throw IllegalArgumentException()
	}
}
