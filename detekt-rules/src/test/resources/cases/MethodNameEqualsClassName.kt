@file:Suppress("unused", "FunctionName")

package cases

class MethodNameEqualsClassName {

	fun MethodNameEqualsClassName() {} // reports 1

	class NestedNameEqualsTopClassName {

		// should not report function with same name in nested class
		fun MethodNameEqualsClassName() {}
	}
}

class MethodNameNotEqualsClassName {

	fun f() {
		fun MethodNameNotEqualsClassName() {}
	}

	object MethodNameEqualsObjectName {

		fun MethodNameEqualsObjectName() {} // reports 1
	}
}

class StaticMethodNameEqualsClassName {

	companion object {
		fun StaticMethodNameEqualsClassName() {} // reports 1
	}
}

class StaticMethodNameEqualsObjectName {

	companion object A {
		fun A() {}
	}
}

interface MethodNameEqualsInterfaceName {

	fun MethodNameEqualsInterfaceName() {}

	class MethodNameEqualsNestedClassName {

		fun MethodNameEqualsNestedClassName() {} // reports 1
	}
}

abstract class BaseClassForMethodNameEqualsClassName {

	abstract fun AbstractMethodNameEqualsClassName()
}

class AbstractMethodNameEqualsClassName : BaseClassForMethodNameEqualsClassName() {

	// reports if overridden functions are not ignored
	override fun AbstractMethodNameEqualsClassName() {}
}
