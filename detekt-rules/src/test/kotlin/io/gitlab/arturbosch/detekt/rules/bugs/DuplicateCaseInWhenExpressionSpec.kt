package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DuplicateCaseInWhenExpressionSpec : Spek({
    val subject by memoized { DuplicateCaseInWhenExpression(Config.empty) }

    describe("Duplicate Case In When Expression rule") {

        it("reports duplicated label in when") {
            val code = """
                fun f() {
                    when (1) {
                        1 -> println()
                        1 -> kotlin.io.println()
                        1, 2 -> println()
                        1, 2 -> kotlin.io.println()
                        else -> println()
                    }
                }"""
            val result = subject.compileAndLint(code)
            assertThat(result).hasSize(1)
            assertThat(result.first().message).isEqualTo("When expression has multiple case statements for 1; 1, 2.")
        }

        it("does not report duplicated label in when") {
            val code = """
                fun f() {
                    when (1) {
                        1 -> println()
                        else -> println()
                    }
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
