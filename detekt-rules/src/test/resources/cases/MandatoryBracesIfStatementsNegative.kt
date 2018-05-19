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
}
