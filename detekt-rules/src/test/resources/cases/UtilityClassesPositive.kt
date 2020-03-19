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

sealed class SealedParent {
    companion object {
        fun create(foo: Int?, bar: String?): SealedParent? =
            when {
                foo != null -> FooChild(foo)
                bar != null -> BarChild(bar)
                else -> null
            }
    }
}

data class FooChild(val foo: Int) : SealedParent()

data class BarChild(val bar: String) : SealedParent()
