package cases

@Suppress("unused", "ConstantConditionIf", "SimplifyBooleanWithConstants")
fun collapsibleIfsPositive() {

	if (true) { // reports 1
		if (1 == 1) {}
		// a comment
	}

	if (true) {
		if (1 == 1) { // reports 1
			if (2 == 2) {}
		}
		println()
	}
}
