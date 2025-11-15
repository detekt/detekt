import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * This code should NOT trigger UnreachableCode.
 * The return value inside withContext is reachable.
 */
suspend fun getFileFromAssets(dispatcher: CoroutineDispatcher = Dispatchers.IO): String {
    return withContext(dispatcher) {
        // This line should NOT be flagged as unreachable
        "result"
    }
}

/**
 * Another example similar to the issue report.
 */
suspend fun processData(dispatcher: CoroutineDispatcher): Int {
    return withContext(dispatcher) {
        // This should NOT be unreachable
        val result = 42
        result
    }
}
