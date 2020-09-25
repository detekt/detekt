package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoLineBreakBeforeAssignment
import io.gitlab.arturbosch.detekt.test.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class FormattingRuleSpec : Spek({

    val subject by memoized { NoLineBreakBeforeAssignment(Config.empty) }

    describe("formatting rules can be suppressed") {

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

    describe("formatting rules have a signature") {

        it("has no package name") {
            val findings = subject.lint("""
                fun main() 
                = Unit
            """.trimIndent())

            assertThat(findings.first().signature).isEqualTo("Test.kt:2")
        }

        it("has a package name") {
            val findings = subject.lint("""
                package test.test.test
                fun main() 
                = Unit
            """.trimIndent())

            assertThat(findings.first().signature).isEqualTo("test.test.test.Test.kt:3")
        }
    }

    test("#3063: formatting issues have an absolute path") {
        val expectedPath = Paths.get("/root/kotlin/test.kt").toString()

        val findings = subject.lint(
            """
                fun main()
                = Unit
                """,
            expectedPath
        )

        assertThat(findings.first().location.file).isEqualTo(expectedPath)
    }
})
