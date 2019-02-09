@file:Suppress("unused", "ConstantConditionIf", "RedundantOverride")

package cases

open class NoExceptionRaisedInMethods {

    init {
        throw IllegalStateException()
    }

    override fun toString(): String {
        return super.toString()
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    companion object {
        init {
            throw IllegalStateException()
        }
    }

    fun doSomeEqualsComparison() {
        throw IllegalStateException()
    }

    protected fun finalize() {
    }
}
