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
        // jump statements of the inner loop must not be counted in the outer loop
        while (i > 1) {
            if (i > 1) continue
        }
    }
}
