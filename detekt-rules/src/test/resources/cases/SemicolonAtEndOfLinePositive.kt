package cases

class SemicolonAtEndOfLinePositive {
	fun methodOk() {
		// A comment;
		println("A message")

		println("Another message");
		println("And one more message")
		println("This one"); println("is OK")
	}
}
