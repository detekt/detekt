package cases

import io.gitlab.arturbosch.detekt.print
import java.util.*

/**
 * @author Artur Bosch
 */
@Suppress("unused")
class Default {

	/**
	 * Used for wildcard import spec
	 */
	fun noop() {
		ArrayList<Any>()
	}

	/**
	 * 8, no else
	 */
	fun IfTHenElse() {
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

	fun NoElseInWhen() {
		// no else in when
		when(true) {
			false -> print()
		}
	}

	/**
	 * 3
	 */
	fun letStatements() {
		5.let {
			it.print()
		}
	}

	/**
	 * 8, no finally
	 */
	fun tryStatements() {
		try {
			5.print()
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