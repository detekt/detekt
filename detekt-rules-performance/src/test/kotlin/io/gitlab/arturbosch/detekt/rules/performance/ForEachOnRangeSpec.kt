package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ForEachOnRangeSpec : Spek({

    describe("ForEachOnRange rule") {

        context("a kt file with using a forEach on a range") {
            val code = """
            fun test() {
                (1..10).forEach {
                    println(it)
                }
                (1 until 10).forEach {
                    println(it)
                }
                (10 downTo 1).forEach {
                    println(it)
                }
                (10 downTo 1 step 2).forEach {
                    println(it)
                }
            }
        """

            it("should report the forEach usage") {
                val findings = ForEachOnRange().compileAndLint(code)
                assertThat(findings).hasSize(4)
            }
        }

        context("a kt file with using any other method on a range") {
            val code = """
            fun test() {
                (1..10).isEmpty()
            }
        """

            it("should report not report any issues") {
                val findings = ForEachOnRange().compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a kt file with using a forEach on a list") {
            val code = """
            fun test() {
                listOf(1, 2, 3).forEach {
                    println(it)
                }
            }
        """

            it("should report not report any issues") {
                val findings = ForEachOnRange().compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }
    }
})
