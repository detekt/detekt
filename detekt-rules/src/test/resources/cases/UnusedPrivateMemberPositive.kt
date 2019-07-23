// we can't suppress warnings from intellij here as we support UNUSED_VARIABLE as an alias

// reports 1 violation for every unused* element
class UnusedPrivateMemberPositive {
    private val unusedField = 5
    val publicField = 2
    private val clashingName = 4
    private fun unusedFunction(unusedParam: Int) {
        val unusedLocal = 5
    }
}

object UnusedPrivateMemberPositiveObject {
    private const val unusedObjectConst = 2
    private val unusedField = 5
    private val clashingName = 5
    val useForClashingName = clashingName
    private val unusedObjectField = 4

    object Foo {
        private val unusedNestedVal = 1
    }
}

private fun unusedTopLevelFunction() = 5

private val usedTopLevelVal = 1
private const val unusedTopLevelConst = 1
private val unusedTopLevelVal = usedTopLevelVal

private class ClassWithSecondaryConstructor {
    constructor(used: Any, unused: Any) {
        used.toString()
    }

    // this is actually unused, but clashes with the other constructor
    constructor(used: Any)
}

fun main(args: Array<String>) {
    println("")
}

private fun unusedAndNotCalledFromMain() {} // unused
