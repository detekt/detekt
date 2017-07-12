package cases

/**
 * @author Artur Bosch
 */
@Suppress("unused")
class NestedLongMethods {
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
		fun localLPL(a: Int, b: Int, c: Int, d: Int, e: Int, s: String) {
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
			println("$a,$b,$c,$d,$e,$s")
		}
	}
}
