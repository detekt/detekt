package cases

@Suppress("unused", "ConstantConditionIf")
fun mandatoryBracesIfStatementNegative() {
	if (true) {
		println()
	}

	if (true)
	{
		println()
	}

	if (true) println()

	if (true) { println() }

	if (true) println() else println()

	if (true) println() else if (false) println() else println()
}
