package cases

@Suppress("unused", "UNREACHABLE_CODE")
class UnreachableCode {

	fun return1(p: Int) {
		if (p == 0) {
			return
			println() // report 1 - unreachable code
		}
	}

	fun return2(p: String) : Boolean {
		p.let {
			return it.length < 3
			println() // report 1 - unreachable code
		}
		return false
	}

	fun returnLabel1(ints: List<Int>): List<Int> {
		return ints.map f@{
			if (it == 0) {
				return@f 0
				println() // report 1 - unreachable code
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
			println() // report 1 - unreachable code
		}
		throw IllegalArgumentException()
	}

	fun breakAndContinue() {
		for (i in 1..2) {
			break
			println() // report 1 - unreachable code
		}
		for (i in 1..2) {
			continue
			println() // report 1 - unreachable code
		}
	}
}
