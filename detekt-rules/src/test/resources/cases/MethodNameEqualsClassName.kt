@file:Suppress("unused", "FunctionName")

package cases

class MethodNameEqualsClassName { // reports 1

	fun MethodNameEqualsClassName() {}

	class NestedNameEqualsTopClassName {

		// should not report function with same name in nested class
		fun MethodNameEqualsClassName() {}
	}
}

class MethodNameNotEqualsClassName {

	fun f() {
		fun MethodNameNotEqualsClassName() {}
	}

	object MethodNameEqualsObjectName { // reports 1

		fun MethodNameEqualsObjectName() {}
	}
}

class StaticMethodNameEqualsClassName { // reports 1

	companion object {
		fun StaticMethodNameEqualsClassName() {}
	}
}

class StaticMethodNameEqualsObjectName {

	companion object A {
		fun A() {}
	}
}

interface MethodNameEqualsInterfaceName {

	fun MethodNameEqualsInterfaceName() {}

	class MethodNameEqualsNestedClassName { // reports 1

		fun MethodNameEqualsNestedClassName() {}
	}
}

abstract class BaseClassForMethodNameEqualsClassName {

	abstract fun AbstractMethodNameEqualsClassName()
}

class AbstractMethodNameEqualsClassName : BaseClassForMethodNameEqualsClassName() {

	override fun AbstractMethodNameEqualsClassName() {}
}
