package cases

@Suppress("unused", "ConstantConditionIf", "SimplifyBooleanWithConstants")
fun collapsibleIfsPositive() {

	if (true) { // reports 1 - if statements could be merged
		if (1 == 1) {}
		// a comment
	}

	if (true) {
		if (1 == 1) { // reports 1 - if statements could be merged
			if (2 == 2) {}
		}
		println()
	}
}
