package cases

@Suppress("unused", "UNUSED_VARIABLE")
class LabeledOuterNegative {

	inner class Inner {

		fun foo() {
			print(this@LabeledOuterNegative)
		}

		inner class InnerInner {
			val a = 0

			fun foo() {
				print(this@LabeledOuterNegative)
				print(this@Inner)
			}

			fun Int.extensionMethod() {
				print(this@Inner)
				print(this@InnerInner)
			}
		}
	}

	class Nested {

		fun Int.extensionMethod() {
			print(this@Nested)
		}
	}
}
