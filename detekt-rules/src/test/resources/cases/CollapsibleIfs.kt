package cases

@Suppress("unused", "ConstantConditionIf", "SimplifyBooleanWithConstants", "RedundantSemicolon")
fun collapsibleIfs() {

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

	if (true) {}
	else if (1 == 1) {
		if (true) {}
	}

	if (true) {
		if (1 == 1) {}
	} else {}

	if (true) {
		if (1 == 1) {}
	} else if (false) {}
	else {}


	if (true) {
		if (1 == 1) ;
		println()
	}
}
