package cases

@Suppress("unused", "UNREACHABLE_CODE")
class UnreachableCode {

	fun return1(p: Int) {
		if (p == 0) {
			return
			println() // unreachable
		}
	}

	fun return2(p: String) : Boolean {
		p.let {
			return it.length < 3
			println() // unreachable
		}
		return false
	}

	fun returnLabel1(ints: List<Int>): List<Int> {
		return ints.map f@{
			if (it == 0) {
				return@f 0
				println() // unreachable
			}
			return@f 1
		}
	}

	fun returnLabel2(ints: List<Int>) {
		ints.forEach {
			if (it == 0) return@forEach
			println()
		}
	}

	fun throw1(p: Int): Int {
		if (p == 0) {
			throw IllegalArgumentException()
			println() // unreachable
		}
		throw IllegalArgumentException()
	}
}
