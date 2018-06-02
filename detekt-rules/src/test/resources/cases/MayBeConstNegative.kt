package cases


/**
 * @author Artur Bosch
 */
class MyClass {
	companion object {
		val const = "${MyClass::class.java.name}.EXTRA_DETAILS"
		private val A = "asdf=${AnotherClass.staticVariable}"
	}
}

class AnotherClass {
	companion object {
		const val staticVariable = ""
	}
}

var test_var = "test"
val code = """
  object Test {
    val TEST = "Test $test_var"
  }
"""

