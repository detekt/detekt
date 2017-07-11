package cases

/**
 * @author Artur Bosch
 */
@Suppress("UNUSED_EXPRESSION", "UNREACHABLE_CODE", "unused")
fun stuff(): Int {
	return try {
		return if (true) {
			if (false) return -1
			return 5
		} else {
			5
			return try {
				"5".toInt()
			} catch (e: IllegalArgumentException) {
				5
			} catch (e: RuntimeException) {
				3
				return 5
			}
		}
	} catch (e: Exception) {
		when(5) {
			5 -> return 1
			2 -> return 1
			else -> 5
		}
		return 7
	}
}
