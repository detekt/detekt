@file:Suppress("unused", "ConvertSecondaryConstructorToPrimary", "UNUSED_PARAMETER")

package cases

class UtilityClassWithPrimaryPrivateConstructorOk private constructor() {

    companion object {
        val C = 0
    }
}

class UtilityClassWithPrimaryInternalConstructorOk internal constructor() {

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

class NoUtilityClassBecauseOfInterface : InterfaceWithCompanionObject {

    constructor()

    companion object {
        val C = 0
    }
}

open class UtilityClassesNegativeParent(val i: Int)
class NoUtilityClassBecauseOfInheritance : UtilityClassesNegativeParent {

    constructor(i: Int) : super(i)

    companion object {
        val C = 0
    }
}

class NoUtilityClasses {

    private val i = 0

    class EmptyClass1 {}
    class EmptyClass2

    class ClassWithSecondaryConstructor {
        constructor()
    }

    class ClassWithInstanceFunc {

        fun f() {}

        companion object {
            val C = 0
        }
    }

    class ClassWithPrimaryConstructorParameter1(val i: Int) {

        companion object {
            val C = 0
        }
    }

    class ClassWithPrimaryConstructorParameter2 constructor(val i: Int) {

        companion object {
            val C = 0
        }
    }

    class ClassWithSecondaryConstructorParameter {

        constructor(i: Int)

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
