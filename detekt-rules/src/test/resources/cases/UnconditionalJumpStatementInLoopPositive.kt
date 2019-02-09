@file:Suppress("unused", "UNREACHABLE_CODE")

package cases

fun unconditionalJumpStatementsInLoop() { // reports 5 - 1 for every jump statement
    for (i in 1..2) break
    for (i in 1..2) continue
    for (i in 1..2) return
    while (true) break
    do {
        break
    } while (true)
}

fun unconditionalJumpStatementsInLoop2() {
    for (i in 1..2) {
        break // reports 1 - dead code
        print("")
    }
    for (i in 1..2) {
        print("")
        break // reports 1
    }
}

fun unconditionalJumpStatementInNestedLoop() { // reports 1
    for (i in 1..2) {
        for (j in 1..2) {
            break
        }
    }
}
