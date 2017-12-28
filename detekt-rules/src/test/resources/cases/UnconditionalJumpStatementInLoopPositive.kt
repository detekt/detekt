@file:Suppress("unused", "UNREACHABLE_CODE")

package cases

fun unconditionalJumpStatementsInLoop() { // reports 6
	for (i in 1..2) break
	for (i in 1..2) continue
	for (i in 1..2) return
	for (i in 1..2) throw IllegalStateException()
	while (true) break
	do {
		break
	} while (true)
}

fun unconditionalJumpStatementInNestedLoop() { // reports 1
	for (i in 1..2) {
		for (j in 1..2) {
			break
		}
	}
}
