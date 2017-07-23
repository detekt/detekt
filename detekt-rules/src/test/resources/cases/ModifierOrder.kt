package cases

abstract internal class Test(val test: String = "abc"): OpenTest() {
	lateinit public var property: String

	open override fun test() {
	}
}

abstract class OpenTest {
	abstract fun test()
}
