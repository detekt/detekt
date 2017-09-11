package cases

@Suppress("unused")
class UnconditionalJumpStatementInLoop {

	fun jumpStatementsInLoop() { // reports 6
		for (i in 1..2) break
		for (i in 1..2) continue
		for (i in 1..2) return
		for (i in 1..2) throw IllegalStateException()
		while (true) break
		do {
			break
		} while (true)
	}

	fun jumpStatementInNestedLoop() { // reports 1
		for (i in 1..2) {
			for (j in 1..2) {
				break
			}
		}
	}

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
}
