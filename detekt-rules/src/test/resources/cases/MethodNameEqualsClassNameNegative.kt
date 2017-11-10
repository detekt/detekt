@file:Suppress("unused", "FunctionName")

package cases

class MethodNameNotEqualsClassName {

	// should not report a nested function with the same name as the class
	fun nestedFunction() {
		fun MethodNameNotEqualsClassName() {}
	}

	class NestedNameEqualsTopClassName {

		// should not report function with same name in nested class
		fun MethodNameNotEqualsClassName() {}
	}
}

class StaticMethodNameEqualsObjectName {

	companion object A {
		fun A() {}
	}
}

abstract class BaseClassForMethodNameEqualsClassName {

	abstract fun AbstractMethodNameEqualsClassName()
}

interface MethodNameEqualsInterfaceName {

	fun MethodNameEqualsInterfaceName() {}
}
