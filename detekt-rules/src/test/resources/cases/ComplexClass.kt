package cases

import org.jetbrains.kotlin.utils.sure

/**
 * @author Artur Bosch
 */
@Suppress("unused")
class ComplexClass {// McCabe: 32
	fun complex() {
		try {//4
			while (true) {
				if (true) {
					when("string") {
						"" -> println()
						else -> println()
					}
				}
			}
		} catch (ex: Exception) {
			try {//3
				println()
			} catch (ex: Exception) {
				while (true) {
					if (false) {
						println()
					} else {
						println()
					}
				}
			}
		} finally {
			try {//3
				println()
			} catch (ex: Exception) {
				while (true) {
					if (false) {
						println()
					} else {
						println()
					}
				}
			}
		}
		(1..10).forEach {//1
			println()
		}
		for (i in 1..10) {//1
			println()
		}
	}

	fun manyClosures() {//5
		true.let {
			true.apply {
				true.run {
					true.sure {
						""
					}
				}
			}
		}
	}
	fun manyClosures2() {//5
		true.let {
			true.apply {
				true.run {
					true.sure {
						""
					}
				}
			}
		}
	}
	fun manyClosures3() {//5
		true.let {
			true.apply {
				true.run {
					true.sure {
						""
					}
				}
			}
		}
	}
	fun manyClosures4() {//5
		true.let {
			true.apply {
				true.run {
					true.sure {
						""
					}
				}
			}
		}
	}
}