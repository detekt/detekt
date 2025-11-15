import kotlinx.coroutines.delay

/**
 * Helper function that suspends.
 */
suspend fun suspendingHelper(): String {
    delay(100)
    return "result"
}

/**
 * This function should NOT be flagged as having redundant suspend modifier.
 * It calls another suspend function.
 */
suspend fun caller(): String {
    return suspendingHelper()
}

/**
 * Another example with multiple suspend calls.
 */
suspend fun multipleCallsExample(): String {
    val first = suspendingHelper()
    val second = suspendingHelper()
    return first + second
}
