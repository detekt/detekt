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

// reports 1
fun complexMethodWith2Statements(i: Int) {
	when (i) {
		1 -> print("one")
		2 -> print("two")
		3 -> print("three")
		else -> print(i)
	}
	if (i == 1) { }
}
