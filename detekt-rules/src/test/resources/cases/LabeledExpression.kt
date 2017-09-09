package cases

@Suppress("unused")
fun breakWithLabel() { // reports 2
	loop@ for (i in 1..100) {
		for (j in 1..100) {
			if (j == 5) break@loop
		}
	}
}

@Suppress("unused")
fun continueWithLabel() { // reports 2
	loop@ for (i in 1..100) {
		for (j in 1..100) {
			if (j == 5) continue@loop
		}
	}
}

@Suppress("unused")
fun implicitReturnWithLabel(range: IntRange) { // reports 1
	range.forEach {
		if (it == 5) return@forEach
		println(it)
	}
}

@Suppress("unused")
fun returnWithLabel(range: IntRange) {  // reports 2
	range.forEach label@ {
		if (it == 5) return@label
		println(it)
	}
}
