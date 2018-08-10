@file:Suppress("unused", "ConvertSecondaryConstructorToPrimary", "RemoveEmptyPrimaryConstructor")

package cases

class UtilityClassWithDefaultConstructor { // violation

	companion object {
		val C = 0
	}
}

class UtilityClassWithPrimaryConstructor1 constructor() { // violation

	companion object {
		val C = 0
	}
}

class UtilityClassWithPrimaryConstructor2() { // violation

	companion object {
		val C = 0
	}
}

class UtilityClassWithSecondaryConstructor { // violation

	constructor()

	companion object {
		val C = 0
	}
}

class UtilityClassWithEmptyCompanionObj { // violation

	companion object
}

open class OpenUtilityClass { // violation - utility class should be final

	internal constructor()

	companion object {
		val C = 0
	}
}
