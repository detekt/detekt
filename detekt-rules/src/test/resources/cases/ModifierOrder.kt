@file:Suppress("RedundantVisibilityModifier", "unused", "RedundantModalityModifier")

package cases

abstract public class Test(val test: String = "abc"): OpenTest() {
	lateinit public var property: String

	override open fun test() {
	}
}

abstract class OpenTest {
	abstract fun test()
}
