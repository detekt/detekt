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
        try {
        } catch (f: IllegalStateException) {
            throw f // reports 1 - dead code after the same exception is rethrown
        }
    }
}
