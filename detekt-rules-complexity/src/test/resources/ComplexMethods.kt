@file:Suppress("unused")

package cases

// reports 1 - only if ignoreSingleWhenExpression = false
fun complexMethodWithSingleWhen1(i: Int) =
    when (i) {
        1 -> print("one")
        2 -> print("two")
        3 -> print("three")
        else -> print(i)
    }

// reports 1 - only if ignoreSingleWhenExpression = false
fun complexMethodWithSingleWhen2(i: Int) {
    when (i) {
        1 -> print("one")
        2 -> print("two")
        3 -> print("three")
        else -> print(i)
    }
}

// reports 1 - only if ignoreSingleWhenExpression = false
fun complexMethodWithSingleWhen3(i: Int): String {
    return when (i) {
        1 -> "one"
        2 -> "two"
        3 -> "three"
        else -> ""
    }
}

// reports 1 - only if ignoreSingleWhenExpression = false
fun complexMethodWithSingleWhen4(i: Int) = when (i) {
    1 -> "one"
    2 -> "two"
    3 -> "three"
    else -> ""
}

// reports 1
fun complexMethodWith2Statements(i: Int) {
    when (i) {
        1 -> print("one")
        2 -> print("two")
        3 -> print("three")
        else -> print(i)
    }
    if (i == 1) {
    }
}
