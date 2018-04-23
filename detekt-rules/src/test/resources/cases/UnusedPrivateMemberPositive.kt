// we can't suppress warnings from intellij here as we support UNUSED_VARIABLE as an alias

class UnusedPrivateMemberPositive {
	private val unusedField = 5

	private fun unusedFunction(unusedParam: Int) {
		val unusedLocal = 5
	}
}

private fun unusedTopLevelFunction() = 5
