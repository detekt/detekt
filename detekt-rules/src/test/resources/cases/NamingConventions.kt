@file:Suppress("unused", "RemoveEmptyClassBody")

package cases

// both valid
val variable = 5
val _variable = 5

//invalid
val V_riable = 5
// invalid start with _ is optional, but then lowercase!
val _Variable = 5
// topLevel vars count as if were declared in objects
val ALLOWED_AS_UPPERCASE = 5

//valid
fun fileMethod() {
}

//invalid
fun FileMethod() {
}

//invalid
fun _fileMethod() {
}

class NamingConventions {

	//invalid
	val C_lassVariable = 5
	//invalid
	val CLASSVARIABLE = 5
	//valid
	val _classVariable = 5
	//valid
	val classVariable = 5

	//valid
	fun classMethod() {
	}

	fun underscoreTestMethod() {
		val (_, status) = Pair(1, 2) // _ should not be reported
	}

	//invalid
	fun _classmethod() {
	}

	//invalid
	fun Classmethod() {
	}

	//valid
	companion object {
		//invalid
		const val stuff = "stuff"
	}
}

//invalid
class _NamingConventions {}

//invalid
class namingConventions {}
