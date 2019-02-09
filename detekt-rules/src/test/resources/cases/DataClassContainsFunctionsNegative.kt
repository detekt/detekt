@file:Suppress("unused", "RedundantOverride")

package cases

data class ValidDataClass(val i: Int)

data class DataClassWithOverriddenMethods(val i: Int) {

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun toString(): String {
        return super.toString()
    }
}

class ClassWithRegularFunctions {

    fun f1() {}
    fun f2() {}
}
