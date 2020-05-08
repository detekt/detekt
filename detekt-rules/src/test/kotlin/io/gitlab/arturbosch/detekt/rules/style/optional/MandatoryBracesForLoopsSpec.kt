package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MandatoryBracesForLoopsSpec : Spek({
    val subject by memoized { MandatoryBracesForLoops() }

    describe("reports multi-line for loops should have braces") {

        it("does not report with braces") {
            val code = """
            fun test() {
                for (i in 0..10) {
                    println(i)
                }
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report full loop on single line") {
            val code = """
            fun test() {
                for (i in 0..10) println(i)
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports multi-line without braces") {
            val code = """
            fun test() {
                for (i in 0..10)
                    println(i)
            }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0].id).isEqualTo("MandatoryBracesForLoops")
            assertThat(findings[0].entity.ktElement?.text).isEqualTo("println(i)")
        }

        it("does not report on suppression") {
            val code = """
            fun test() {
                @Suppress("MandatoryBracesForLoops")
                for (i in 0..10)
                    println(i)
            }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
