package cases

@Suppress("unused")
class UtilityClassWithDefaultConstructor { // violation

	companion object {
		val C = 0
	}
}

@Suppress("unused")
class UtilityClassWithPrimaryConstructor1 constructor() { // violation

	companion object {
		val C = 0
	}
}

@Suppress("unused")
class UtilityClassWithPrimaryConstructor2() { // violation

	companion object {
		val C = 0
	}
}

@Suppress("unused")
class UtilityClassWithPrimaryConstructorOk private constructor() {

	companion object {
		val C = 0
	}
}

@Suppress("unused")
class UtilityClassWithSecondaryConstructor { // violation

	public constructor()

	companion object {
		val C = 0
	}
}

@Suppress("unused")
class UtilityClassWithSecondaryConstructorOk {

	private constructor()

	companion object {
		val C = 0
	}
}

@Suppress("unused")
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

@Suppress("unused")
interface InterfaceWithCompanionObject {

	companion object {
		val C = 0
	}
}
