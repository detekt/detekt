package cases

import org.jetbrains.kotlin.utils.sure

/**
 * @author Artur Bosch
 */
@Suppress("unused")
class ComplexClass {// McCabe: 56, LLOC: 20 + 20 + 4x4

	class NestedClass { //20
		fun complex() { //1 +
			try {//5
				while (true) {
					if (true) {
						when ("string") {
							"" -> println()
							else -> println()
						}
					}
				}
			} catch (ex: Exception) { //1 + 5
				try {
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
			} finally { // 6
				try {
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
			(1..10).forEach { //1
				println()
			}
			for (i in 1..10) { //1
				println()
			}
		}
	}

	fun complex() { //1 +
		try {//5
			while (true) {
				if (true) {
					when ("string") {
						"" -> println()
						else -> println()
					}
				}
			}
		} catch (ex: Exception) { //1 + 5
			try {
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
		} finally { // 6
			try {
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
		(1..10).forEach { //1
			println()
		}
		for (i in 1..10) { //1
			println()
		}
	}

	fun manyClosures() {//4
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

	fun manyClosures2() {//4
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

	fun manyClosures3() {//4
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

	fun manyClosures4() {//4
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
