package io.gitlab.arturbosch.detekt.core.suppressors

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SuppressorsSpec : Spek({

    describe("test getSuppressors") {
        val noIgnorableCodeSmell by memoized {
            val root = compileContentForTest(
                """
                fun foo() = Unit
                """.trimIndent()
            )
            CodeSmell(ARule().issue, Entity.from(root), "")
        }
        val ignorableCodeSmell by memoized {
            val root = compileContentForTest(
                """
                @file:Composable
                fun foo() = Unit
                """.trimIndent()
            )
            CodeSmell(ARule().issue, Entity.from(root), "")
        }

        it("A finding that should be suppressed") {
            val rule = ARule(TestConfig("ignoreAnnotated" to listOf("Composable")))
            val suppress = getSuppressors(rule)
                .fold(false) { acc, suppressor -> acc || suppressor.shouldSuppress(noIgnorableCodeSmell) }

            assertThat(suppress).isFalse()
        }
        it("A finding that should not be suppressed") {
            val rule = ARule(TestConfig("ignoreAnnotated" to listOf("Composable")))
            val suppress = getSuppressors(rule)
                .fold(false) { acc, suppressor -> acc || suppressor.shouldSuppress(ignorableCodeSmell) }

            assertThat(suppress).isTrue()
        }

        context("MultiRule") {
            it("A finding that should be suppressed") {
                val rule = AMultiRule(TestConfig("ignoreAnnotated" to listOf("Composable")))
                val suppress = getSuppressors(rule)
                    .fold(false) { acc, suppressor -> acc || suppressor.shouldSuppress(noIgnorableCodeSmell) }

                assertThat(suppress).isFalse()
            }
            it("A finding that should not be suppressed") {
                val rule = AMultiRule(TestConfig("ignoreAnnotated" to listOf("Composable")))
                val suppress = getSuppressors(rule)
                    .fold(false) { acc, suppressor -> acc || suppressor.shouldSuppress(ignorableCodeSmell) }

                assertThat(suppress).isTrue()
            }
        }
    }
})

private class AMultiRule(config: Config) : MultiRule() {
    override val rules: List<Rule> = listOf(ARule(config))
}

private class ARule(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue("IssueId", Severity.CodeSmell, "", Debt.TWENTY_MINS)
}
