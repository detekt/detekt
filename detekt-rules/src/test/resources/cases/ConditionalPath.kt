@file:Suppress("UNREACHABLE_CODE", "UNUSED_EXPRESSION", "ConstantConditionIf", "unused")

package cases

fun conditionalFunction1(): Int { // gets counted by ConditionalPathVisitor
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
        when (5) {
            5 -> return 1
            2 -> return 1
            else -> 5
        }
        return 7
    }
}

fun conditionalFunction2(): Int = try {
    if (true) {
        if (false) -1
        5
    } else {
        5
        try {
            "5".toInt()
        } catch (e: IllegalArgumentException) {
            5
        } catch (e: RuntimeException) {
            3
            5
        }
    }
} catch (e: Exception) {
    when (5) {
        5 -> 1
        2 -> 1
        else -> 5
    }
    7
}

fun conditionalForWhenStatement(i: Int): Int {
    return when (i) {
        1 -> 1
        else -> 2
    }
}
