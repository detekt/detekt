package cases

@Suppress("unused", "UNREACHABLE_CODE", "UNUSED_EXPRESSION")
fun rethrowCaughtExceptionPositive() {
	try {
	} catch (e: IllegalStateException) {
		throw e // reports 1 - the same exception is rethrown
	}
	try {
	} catch (e: IllegalStateException) {
		throw e // reports 1 - dead code after the same exception is rethrown
		print("log")
	}
	try {
	} catch (e: IllegalStateException) {
		print("log") // reports 1 - exception is not logged
		throw e
	}
	try {
	} catch (e: IllegalStateException) {
		print(e.message) // reports 1 - logs only the exception message, exception is lost
		throw e
	}
}
