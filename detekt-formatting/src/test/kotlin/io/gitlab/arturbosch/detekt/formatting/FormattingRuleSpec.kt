package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoLineBreakBeforeAssignment
import io.gitlab.arturbosch.detekt.test.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FormattingRuleSpec : Spek({

    describe("formatting rules can be suppressed") {

        val subject by memoized { NoLineBreakBeforeAssignment(Config.empty) }

        it("does support suppression only on file level") {
            val findings = subject.lint("""
                @file:Suppress("NoLineBreakBeforeAssignment")
                fun main() 
                = Unit
            """.trimIndent())

            assertThat(findings).isEmpty()
        }

        it("does not support suppression on node level") {
            val findings = subject.lint("""
                @Suppress("NoLineBreakBeforeAssignment")
                fun main() 
                = Unit
            """.trimIndent())

            assertThat(findings).hasSize(1)
        }
    }
})

fun main() = Unit
