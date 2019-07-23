@file:Suppress("unused", "UNUSED_PARAMETER")

package cases

fun functionReturningConstantString() = "1" // reports 1

fun functionReturningConstantString(str: String) = "str: $$" // reports 1

fun functionReturningConstantEscapedString(str: String) = "str: \$str" // reports 1

fun functionReturningConstantChar() = '1' // reports 1

fun functionReturningConstantInt(): Int { // reports 1
    return 1
}

@Suppress("EqualsOrHashCode")
open class FunctionReturningConstant {

    open fun f() = 1 // reports 1
    override fun hashCode() = 1 // reports 1
}

interface InterfaceFunctionReturningConstant {

    fun interfaceFunctionWithImplementation() = 1 // reports 1

    class NestedClassFunctionReturningConstant {

        fun interfaceFunctionWithImplementation() = 1 // reports 1
    }
}
