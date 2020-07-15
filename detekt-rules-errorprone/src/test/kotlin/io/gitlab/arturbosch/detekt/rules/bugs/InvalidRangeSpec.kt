package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class InvalidRangeSpec : Spek({
    val subject by memoized { InvalidRange(Config.empty) }

    describe("check for loop conditions") {

        it("does not report correct bounds in for loop conditions") {
            val code = """
                fun f() {
                    for (i in 2..2) {}
                    for (i in 2 downTo 2) {}
                    for (i in 2 until 3) {}
                    for (i in 2 until 4 step 2) {}
                    for (i in (1+1)..3) { }
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports incorrect bounds in for loop conditions") {
            val code = """
                fun f() {
                    for (i in 2..1) { }
                    for (i in 1 downTo 2) { }
                    for (i in 2 until 2) { }
                    for (i in 2 until 1 step 2) { }
                }"""
            assertThat(subject.compileAndLint(code)).hasSize(4)
        }

        it("reports nested loops with incorrect bounds in for loop conditions") {
            val code = """
                fun f() {
                    for (i in 2..2) {
                        for (i in 2..1) { }
                    }
                }"""
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }
    }

    describe("check ranges outside of loops") {

        it("reports for '..'") {
            val code = "val r = 2..1"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report binary expressions without an invalid range") {
            val code = "val sum = 1 + 2"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
