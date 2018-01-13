@file:Suppress("unused", "ConvertSecondaryConstructorToPrimary")

package cases

class UtilityClassWithPrimaryConstructorOk private constructor() {

	companion object {
		val C = 0
	}
}

class UtilityClassWithSecondaryConstructorOk {

	private constructor()

	companion object {
		val C = 0
	}
}

class NoUtilityClasses {

	private val i = 0

	class EmptyClass(val i: Int)

	class NoUtilityClass2(val i: Int) {

		companion object {
			val C = 0
		}
	}

	class NoUtilityClass3 constructor(val i: Int) {

		companion object {
			val C = 0
		}
	}

	companion object {
		val C = 0
	}
}

interface InterfaceWithCompanionObject {

	companion object {
		val C = 0
	}
}

interface SomeInterface
class SomeImplementation : SomeInterface
class NotUtilityClass : SomeInterface by SomeImplementation() {
	// Issue#682 - Class with delegate is no utility class
	companion object {
		val C = 0
	}
}
