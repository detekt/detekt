package cases

@Suppress("unused")
fun rethrowCaughtExceptionNegative() {
	try {
	} catch (e: IllegalStateException) {
		throw IllegalArgumentException(e) // e encapsulated in a new exception is allowed
	}
	try {
	} catch (e: IllegalStateException) {
		throw IllegalArgumentException("msg", e)
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
	try {
	} catch (e: IllegalStateException) {
		print("log") // taking specific action before throwing the exception
		throw e
	}
	try {
	} catch (e: IllegalStateException) {
		print(e.message) // taking specific action before throwing the exception
		throw e
	}
}
