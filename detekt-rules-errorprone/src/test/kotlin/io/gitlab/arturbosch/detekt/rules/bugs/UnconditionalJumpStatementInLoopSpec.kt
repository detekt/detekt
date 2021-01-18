package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnconditionalJumpStatementInLoopSpec : Spek({
    val subject by memoized { UnconditionalJumpStatementInLoop() }

    describe("UnconditionalJumpStatementInLoop rule") {

        it("reports unconditional jumps") {
            val code = """
                fun f() {
                    for (i in 1..2) break
                    for (i in 1..2) continue
                    for (i in 1..2) return
                    while (true) {
                        println("")
                        break
                    }
                    do {
                        break
                        println("")
                    } while (true)
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(5)
        }

        it("reports unconditional jump in nested loop") {
            val code = """
                fun f() {
                    for (i in 1..2) {
                        for (j in 1..2) {
                            break
                        }
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report a conditional jump in a nested block") {
            val code = """
                fun f() {
                    for (i in 1..2) {
                        try { 
                            break
                        } finally {
                        }
                    }
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report a conditional jump in if-else block") {
            val code = """
                fun f() {
                    for (i in 1..2) {
                        if (i > 1) {
                            break
                        }
                        if (i > 1) println() else break
                    }
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report an conditional elvis continue") {
            val findings = subject.compileAndLint("""
                fun main() {
                    fun compute(i: Int) = null
                    for (i in 1..5)  
                        return compute(i) ?: continue
                }
            """)

            assertThat(findings).isEmpty()
        }

        it("reports conditional elvis return") {
            val findings = subject.compileAndLint("""
                fun main() {
                    fun compute(i: Int) = null
                    for (i in 1..5)  
                        return compute(i) ?: return
                }
            """)

            assertThat(findings).hasSize(1)
        }

        it("does not report a return after a conditional jump") {
            val findings = subject.compileAndLint("""
                fun f(): Int {
                    for (i in 0 until 10) {
                        val a = i * i
                        if (a < 27) continue
                        return a
                    }
                    return 0
                }

                fun g(): Int {
                    for (i in 0 until 10) {
                        val a = i * i
                        when {
                            a < 27 -> continue
                        }
                        return a
                    }
                    return 0
                }
            """)

            assertThat(findings).isEmpty()
        }
    }
})
