package cases

import java.util.*

/**
 * @author Artur Bosch
 */
@Suppress("unused", "ConvertLambdaToReference")
class Default {

	fun returnsUnit(): Unit {

	}

	fun nestedBlockDepthOfGreaterThree() {
		if (true) {
			if (true) {
				if (true) {
					if (true) {

					}
				}
			}
		}
	}

	/**
	 * 8, no else
	 */
	fun ifTHenElse() {
		if (true) {
			while (true) {
				println("Loop")
			}
		} else {
			println("NoLoop")
			when ("string") {
				"string" -> println("s")
				else -> println("s")
			}
		}
	}

	/**
	 * 3
	 */
	fun letStatements() {
		5.let {
			println(it)
		}
	}

	/**
	 * 8, no finally
	 */
	fun tryStatements() {
		try {
			println(5)
		} catch (e: Error) {
			print(e)
		} catch (ex: Error) {
			print(ex)
		} finally {
			print("Finally")
		}
	}

	/**
	 * Used for LM and LPL spec
	 * 11
	 */
	fun long(a: Int, b: Int, c: Int, d: Int, e: Int, s: String) {
		println("$a,$b,$c,$d,$e,$s")
		println("$a,$b,$c,$d,$e,$s")
		println("$a,$b,$c,$d,$e,$s")
		println("$a,$b,$c,$d,$e,$s")
		println("$a,$b,$c,$d,$e,$s")
		println("$a,$b,$c,$d,$e,$s")
		println("$a,$b,$c,$d,$e,$s")
		println("$a,$b,$c,$d,$e,$s")
		println("$a,$b,$c,$d,$e,$s")
		println("$a,$b,$c,$d,$e,$s")
	}

	/**
	 * 55
	 */
	fun forLargeClass() {
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
		println()
	}
}
