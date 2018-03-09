package cases

class SemicolonAtEndOfLineNegative {
	fun methodOk() {
		// A comment
		println("A message")
		println()
		println(); println()
		println(); // how to get away this rule
	}
}
