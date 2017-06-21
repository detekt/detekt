package cases

import org.jetbrains.kotlin.utils.sure

/**
 * @author Artur Bosch
 */
@Suppress("unused")
class ComplexClass {// McCabe: 42, LLOC: 1+2+29+29+24

	class NestedClass { //13
		fun complex() {
			try {//4
				while (true) {
					if (true) {
						when ("string") {
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
			(1..10).forEach {
				//1
				println()
			}
			for (i in 1..10) {//1
				println()
			}
		}
	}

	fun complex() { //13
		try {//4
			while (true) {
				if (true) {
					when ("string") {
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
		(1..10).forEach {
			//1
			println()
		}
		for (i in 1..10) {//1
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