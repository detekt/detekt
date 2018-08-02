package cases

@Suppress("unused", "UNUSED_VARIABLE")
class LabeledOuterNegative {

	inner class Inner1 {

		fun foo() {
			val foo = this@LabeledOuterNegative
		}

		inner class Inner2 {
			fun foo() {
				val foo = this@LabeledOuterNegative
				val foo2 = this@Inner1
			}
		}
	}
}
