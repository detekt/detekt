package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoLineBreakBeforeAssignment
import io.gitlab.arturbosch.detekt.test.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class FormattingRuleSpec {

    private lateinit var subject: NoLineBreakBeforeAssignment

    @BeforeEach
    fun createSubject() {
        subject = NoLineBreakBeforeAssignment(Config.empty)
    }

    @Nested
    inner class `formatting rules can be suppressed` {

        @Test
        fun `does support suppression only on file level`() {
            val findings = subject.lint(
                """
                    @file:Suppress("NoLineBreakBeforeAssignment")
                    fun main()
                    = Unit
                """.trimIndent()
            )

            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not support suppression on node level`() {
            val findings = subject.lint(
                """
                    @Suppress("NoLineBreakBeforeAssignment")
                    fun main()
                    = Unit
                """.trimIndent()
            )

            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `formatting rules have a signature` {

        @Test
        fun `has no package name`() {
            val findings = subject.lint(
                """
                    fun main()
                    = Unit
                """.trimIndent()
            )

            assertThat(findings.first().signature).isEqualTo("Test.kt:2")
        }

        @Test
        fun `has a package name`() {
            val findings = subject.lint(
                """
                    package test.test.test
                    fun main()
                    = Unit
                """.trimIndent()
            )

            assertThat(findings.first().signature).isEqualTo("test.test.test.Test.kt:3")
        }
    }

    @Test
    fun `#3063_ formatting issues have an absolute path`() {
        val expectedPath = Paths.get("/root/kotlin/test.kt").toString()

        val findings = subject.lint(
            """
                fun main()
                = Unit
            """,
            expectedPath
        )

        assertThat(findings.first().location.filePath.absolutePath.toString()).isEqualTo(expectedPath)
    }
}
