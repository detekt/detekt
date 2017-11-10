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
