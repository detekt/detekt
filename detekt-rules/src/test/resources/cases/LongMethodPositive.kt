package cases

/**
 * @author Artur Bosch
 */
@Suppress("unused")
class LongMethodPositive {

	fun longMethod() {
		println()
		println()
		println()
		fun localLongMethod() {
			println()
			println()
			println()
		}
	}
}
