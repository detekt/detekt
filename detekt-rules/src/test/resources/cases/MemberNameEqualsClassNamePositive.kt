@file:Suppress("unused", "FunctionName")

package cases

class MethodNameEqualsClassName {

	fun methodNameEqualsClassName() {} // reports 1
}

object MethodNameEqualsObjectName {

	fun MethodNameEqualsObjectName() {} // reports 1
}

class PropertyNameEqualsClassName {

	val propertyNameEqualsClassName = 0 // reports 1
}

object PropertyNameEqualsObjectName {

	val propertyNameEqualsObjectName = 0 // reports 1
}

class StaticMethodNameEqualsClassName {

	companion object {
		fun StaticMethodNameEqualsClassName() {} // reports 1
	}
}

class MethodNameContainer {

	class MethodNameEqualsNestedClassName {

		fun MethodNameEqualsNestedClassName() {} // reports 1
	}
}

class AbstractMethodNameEqualsClassName : BaseClassForMethodNameEqualsClassName() {

	// reports if overridden functions are not ignored
	override fun AbstractMethodNameEqualsClassName() {}
}
