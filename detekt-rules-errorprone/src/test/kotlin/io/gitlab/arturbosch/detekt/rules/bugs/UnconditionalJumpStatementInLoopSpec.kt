package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnconditionalJumpStatementInLoopSpec : Spek({
    val subject by memoized { UnconditionalJumpStatementInLoop() }

    describe("UnconditionalJumpStatementInLoop rule") {

        it("reports an unconditional return in for loop") {
            val code = """
                fun f() {
                    for (i in 1..2) return
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports an unconditional return in while loop") {
            val code = """
                fun f() {
                    while (true) { return } 
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports an unconditional return in do-while loop") {
            val code = """
                fun f() {
                    do { return } while(true) 
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports an unconditional continue in for loop") {
            val code = """
                fun f() {
                    for (i in 1..2) continue
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports an unconditional continue in while loop") {
            val code = """
                fun f() {
                    while (true) { continue } 
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports an unconditional continue in do-while loop") {
            val code = """
                fun f() {
                    do { continue } while(true) 
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports an unconditional break in for loop") {
            val code = """
                fun f() {
                    for (i in 1..2) break
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports an unconditional break in while loop") {
            val code = """
                fun f() {
                    while (true) { break } 
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports an unconditional break in do-while loop") {
            val code = """
                fun f() {
                    do { break } while(true) 
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports an unconditional return in a nested loop") {
            val code = """
                fun f() {
                    for (i in 1..2) {
                        for (j in 1..2) {
                            return
                        }
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports an unconditional continue in a nested loop") {
            val code = """
                fun f() {
                    for (i in 1..2) {
                        for (j in 1..2) {
                            continue
                        }
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports an unconditional break in a nested loop") {
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

        it("does not report a conditional return in an if-else block") {
            val code = """
                fun f() {
                    for (i in 1..2) {
                        if (i > 1) {
                            return
                        }
                        if (i > 1) println() else return
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report a conditional continue in an if-else block") {
            val code = """
                fun f() {
                    for (i in 1..2) {
                        if (i > 1) {
                            continue
                        }
                        if (i > 1) println() else continue
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report a conditional break in an if-else block") {
            val code = """
                fun f() {
                    for (i in 1..2) {
                        if (i > 1) {
                            break
                        }
                        if (i > 1) println() else break
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports an unconditional return after Elvis operator") {
            val code = """
                fun main() {
                    fun compute(i: Int) = null
                    for (i in 1..5)
                        return compute(i) ?: return
                }
            """

            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report a conditional continue after Elvis operator") {
            val code = """
                fun f(): Int {
                    fun compute(i: Int): Int? = null
                    for (i in 1..5) {
                        return compute(i) ?: continue
                    }
                    return 0
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report a conditional break after Elvis operator") {
            val code = """
                fun f(): Int {
                    fun compute(i: Int): Int? = null
                    for (i in 1..5) {
                        return compute(i) ?: break
                    }
                    return 0
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report a conditional return after assignment with Elvis operator") {
            val code = """
                fun f(): Int {
                    fun compute(i: Int): Int? = null
                    for (i in 1..5) {
                        val int = compute(i) ?: return 0
                        return int + 1
                    }
                    return 0
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report a conditional continue after assignment with Elvis operator") {
            val code = """
                fun f(): Int {
                    fun compute(i: Int): Int? = null
                    for (i in 1..5) {
                        val int = compute(i) ?: continue
                        return int + 1
                    }
                    return 0
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report a conditional break after assignment with Elvis operator") {
            val code = """
                fun f(): Int {
                    fun compute(i: Int): Int? = null
                    for (i in 1..5) {
                        val int = compute(i) ?: break
                        return int + 1
                    }
                    return 0
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report a conditional return after expression with Elvis operator") {
            val code = """
                fun f() {
                    fun compute(i: Int): Int? = null
                    for (i in 1..5) {
                        compute(i) ?: return
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report a conditional continue after expression with Elvis operator") {
            val code = """
                fun f() {
                    fun compute(i: Int): Int? = null
                    for (i in 1..5) {
                        compute(i) ?: continue
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report a conditional break after expression with Elvis operator") {
            val code = """
                fun f() {
                    fun compute(i: Int): Int? = null
                    for (i in 1..5) {
                        compute(i) ?: break
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
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
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report a return after a conditional jump") {
            val findings = subject.compileAndLint(
                """
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
            """
            )

            assertThat(findings).isEmpty()
        }
    }
})
