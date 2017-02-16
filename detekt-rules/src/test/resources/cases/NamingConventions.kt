@file:Suppress("unused")

package cases

// both valid
val variable = 5
val _variable = 5
// invalid start with _ is optional, but then lowercase!
val V_riable = 5
val _Variable = 5

//valid
fun fileMethod() {
}
//invalid
fun FileMethod() {
}
fun _fileMethod() {
}

class NamingConventions {

	//invalid
	val C_lassVariable = 5
	//valid
	val _classVariable = 5
	val classVariable = 5
	fun classMethod(){
	}
	//invalid
	fun _classmethod(){
	}
	fun Classmethod(){
	}
}
//invalid
class _NamingConventions{}
class namingConventions{}

object Bla {
	val STUFF = "stuff"
}