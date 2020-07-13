@file:Suppress("unused")

package cases

fun jumpStatementNestedOk() {
    for (i in 1..2) {
        try {
            break
        } finally {
        }
    }
}

fun jumpStatementInIf() {
    for (i in 1..2) {
        if (i > 1) {
            break
        }
        if (i > 1) println() else break
    }
}
