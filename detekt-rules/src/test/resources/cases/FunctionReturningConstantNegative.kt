@file:Suppress("unused")

package cases

fun functionNotReturningConstant1() = 1 + 1

fun functionNotReturningConstant2(): Int {
    return 1 + 1
}

fun functionNotReturningConstantString1(str: String) = "str: $str"
