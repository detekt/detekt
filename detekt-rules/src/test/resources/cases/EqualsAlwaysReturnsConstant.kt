package cases

@Suppress("unused")
class A {
	override fun equals(other: Any?): Boolean {
		return true // violations
	}

	private fun equal() {
	}
}

@Suppress("unused")
object B {
	override fun equals(other: Any?): Boolean {
		return false // violations
	}
}
