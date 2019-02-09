@file:Suppress("unused", "ConstantConditionIf", "RedundantOverride", "EqualsOrHashCode")

package cases

open class ExceptionRaisedInMethods {

    // reports 1 - method should not throw an exception
    override fun toString(): String {
        throw IllegalStateException()
    }

    // reports 1 - method should not throw an exception
    override fun hashCode(): Int {
        throw IllegalStateException()
    }

    // reports 1 - method should not throw an exception
    override fun equals(other: Any?): Boolean {
        throw IllegalStateException()
    }

    // reports 1 - method should not throw an exception
    protected fun finalize() {
        if (true) {
            throw IllegalStateException()
        }
    }
}

object ExceptionRaisedInMethodsObject {

    // reports 1 - method should not throw an exception
    override fun equals(other: Any?): Boolean {
        throw IllegalStateException()
    }
}
