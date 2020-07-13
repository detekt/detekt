package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UntilInsteadOfRangeToSpec : Spek({
    val subject by memoized { UntilInsteadOfRangeTo(Config.empty) }

    describe("UntilInsteadOfRangeTo rule") {

        it("reports for '..'") {
            val code = """
                fun f() {
                    for (i in 0 .. 10 - 1) {}
                }"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("does not report if rangeTo not used") {
            val code = """
                fun f() {
                    for (i in 0 until 10 - 1) {}
                    for (i in 10 downTo 2 - 1) {}
                }"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report if upper value isn't a binary expression") {
            val code = """
                fun f() {
                    for (i in 0 .. 10) {}
                }"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report if not minus one") {
            val code = """
                fun f() {
                    for (i in 0 .. 10 + 1) {}
                    for (i in 0 .. 10 - 2) {}
                }"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports for '..'") {
            val code = "val r = 0 .. 10 - 1"
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("does not report binary expressions without a range operator") {
            val code = "val sum = 1 + 2"
            assertThat(subject.lint(code)).isEmpty()
        }
    }
})
