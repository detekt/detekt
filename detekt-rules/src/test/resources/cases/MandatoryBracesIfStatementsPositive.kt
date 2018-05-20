package cases

@Suppress("unused", "ConstantConditionIf", "CascadeIf")
fun mandatoryBracesIfStatementPositive() {
	if (true)
		println()

	if (true)
		println()
	else
		println()

	if (true)
		println()
	else if (false)
		println()
	else
		println()

	if (true) {
		println()
	} else
		println()
}
