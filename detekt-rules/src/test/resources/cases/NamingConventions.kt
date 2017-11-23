@file:Suppress("unused", "RemoveEmptyClassBody", "UNUSED_VARIABLE")

package cases

val variable = 5

// valid: underscore only allowed for private vars
private val _variable = 5
val _variable2 = 5 // invalid

//invalid: starts with uppercase!
val V_riable = 5
// invalid: start with _ is optional, but then lowercase!
val _Variable = 5
// invalid: top level vars are not constants
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

	// invalid
	val C_lassVariable = 5
	// invalid
	val CLASSVARIABLE = 5
	// valid
	private val _classVariable = 5
	// valid
	val classVariable = 5

	// valid
	fun classMethod() {
	}

	fun underscoreTestMethod() {
		val (_, status) = Pair(1, 2) // valid: _
	}

	//invalid
	fun _classmethod() {
	}

	//invalid
	fun Classmethod() {
	}

	companion object {
		const val stUff = "stuff"
		val SSS = "stuff"
		val ooo = Any()
		val OOO = Any()
		val __bla = Any() //invalid
	}
}

//invalid
class _NamingConventions {}

//invalid
class namingConventions {}
