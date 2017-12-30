@file:Suppress("unused", "FunctionName")

package cases

class MethodNameNotEqualsClassName {

	val prop = 0

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

// factory method can have the same name as the class
class FactoryClass1 {

	companion object {
		fun factoryClass1(): FactoryClass1 {
			return FactoryClass1()
		}
	}
}

// factory method can have the same name as the class
class FactoryClass2 {

	companion object {
		fun factoryClass2() = FactoryClass2()
	}
}

abstract class BaseClassForMethodNameEqualsClassName {

	abstract fun AbstractMethodNameEqualsClassName()
}

interface MethodNameEqualsInterfaceName {

	fun MethodNameEqualsInterfaceName() {}
}
