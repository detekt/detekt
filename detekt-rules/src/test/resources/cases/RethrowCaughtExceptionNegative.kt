package cases

@Suppress("unused")
fun rethrowCaughtExceptionNegative() {
	try {
	} catch (e: IllegalStateException) {
		throw IllegalArgumentException(e) // e encapsulated in a new exception is allowed
	}
	try {
	} catch (e: IllegalStateException) {
		print(e) // logging an exception is allowed
	}
	try {
	} catch (e: IllegalStateException) {
		print(e) // logging an exception is allowed
		throw e
	}
}
