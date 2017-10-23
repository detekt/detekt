@file:Suppress("unused", "UNUSED_PARAMETER")

package cases

fun functionReturningConstantString() = "1" // reports

fun functionReturningConstantString(str: String) = "str: $$" // reports

fun functionReturningConstantEscapedString(str: String) = "str: \$str"

fun functionReturningConstantChar() = '1' // reports

fun functionReturningConstantInt(): Int { // reports
	return 1
}

fun functionNotReturningConstant1() = 1 + 1

fun functionNotReturningConstant2(): Int {
	return 1 + 1
}

fun functionNotReturningConstantString1(str: String) = "str: $str"

@Suppress("EqualsOrHashCode")
class FunctionReturningConstant {
	override fun hashCode() = 1
}
