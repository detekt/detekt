@file:Suppress("unused", "UNUSED_VARIABLE")

package cases

fun breakWithLabel() { // reports 2 - for each @label
	loop@ for (i in 1..100) {
		for (j in 1..100) {
			if (j == 5) break@loop
		}
	}
}

fun continueWithLabel() { // reports 2 - for each @label
	loop@ for (i in 1..100) {
		for (j in 1..100) {
			if (j == 5) continue@loop
		}
	}
}

fun implicitReturnWithLabel(range: IntRange) { // reports 1
	range.forEach {
		if (it == 5) return@forEach
		println(it)
	}
}

fun returnWithLabel(range: IntRange) {  // reports 2 - for each @label
	range.forEach label@ {
		if (it == 5) return@label
		println(it)
	}
}

class LabeledOuterPositive {

	inner class Inner1 {

		fun referenceItself() {
			val foo = this@Inner1 // reports 1 - inner class referencing itself
		}

		fun labelShadowing() {
			// reports 1 - new label with the same as the outer class
			emptyList<Int>().forEach LabeledOuterPositive@ {
				// reports 1 - references forEach label and not outer class
				if (it == 5) return@LabeledOuterPositive
				println(it)
			}
		}
	}
}
