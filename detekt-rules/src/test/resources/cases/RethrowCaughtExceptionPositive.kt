package cases

@Suppress("unused", "UNREACHABLE_CODE")
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
}
