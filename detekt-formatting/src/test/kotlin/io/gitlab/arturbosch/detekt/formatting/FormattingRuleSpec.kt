package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoLineBreakBeforeAssignment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class FormattingRuleSpec {

    private lateinit var subject: NoLineBreakBeforeAssignment

    @BeforeEach
    fun createSubject() {
        subject = NoLineBreakBeforeAssignment(Config.empty)
    }

    @Nested
    inner class `formatting rules can be suppressed` {

        @Test
        fun `does support suppression on file level`() {
            val findings = subject.compileAndLint(
                """
                    @file:Suppress("NoLineBreakBeforeAssignment")
                    fun main()
                    = Unit
                """.trimIndent()
            )

            assertThat(findings).isEmpty()
        }

        @Test
        fun `support suppression on node level`() {
            val findings = subject.compileAndLint(
                """
                    @Suppress("NoLineBreakBeforeAssignment")
                    fun main()
                    = Unit
                """.trimIndent()
            )

            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `formatting rules have a signature` {

        @Test
        fun `in a file without package name`() {
            val findings = subject.compileAndLint(
                """
                    fun main()
                    = Unit
                """.trimIndent()
            )

            assertThat(findings.first().signature).isEqualTo("Test.kt\$=")
        }

        @Test
        fun `with a file with package name`() {
            val findings = subject.compileAndLint(
                """
                    package test.test.test
                    fun main()
                    = Unit
                """.trimIndent()
            )

            assertThat(findings.first().signature).isEqualTo("Test.kt\$=")
        }
    }

    @Test
    fun `#3063_ formatting issues have an absolute path`() {
        val expectedPath = Path("/root/kotlin/test.kt").toString()

        val findings = subject.lint(
            """
                fun main()
                = Unit
            """.trimIndent(),
            expectedPath
        )

        assertThat(findings.first().location.filePath.absolutePath.toString()).isEqualTo(expectedPath)
    }
}
