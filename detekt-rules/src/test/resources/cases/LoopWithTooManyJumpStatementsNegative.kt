@file:Suppress("unused")

package cases

fun onlyOneJump() {
	for (i in 1..2) {
		if (i > 1) break
	}
}

fun jumpsInNestedLoops() {
	for (i in 1..2) {
		if (i > 1) break
		while (i > 1) {
			if (i > 1) continue
		}
	}
}
