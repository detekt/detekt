package io.gitlab.arturbosch.detekt.formatting

import io.github.detekt.test.utils.compileForTest
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.ChainWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoLineBreakBeforeAssignment
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.location
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.absolute

class FormattingRuleSpec {

    private lateinit var subject: NoLineBreakBeforeAssignment

    @BeforeEach
    fun createSubject() {
        subject = NoLineBreakBeforeAssignment(Config.empty)
    }

    @Nested
    inner class `formatting rules can be suppressed` {

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

            assertThat(findings.first().entity.signature).isEqualTo("Test.kt\$=")
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

            assertThat(findings.first().entity.signature).isEqualTo("Test.kt\$=")
        }
    }

    @Test
    fun `#3063_ formatting issues have an absolute path`() {
        val expectedPath = Path("src/test/resources/configTests/chain-wrapping-before.kt").absolute()

        val rule = ChainWrapping(Config.empty)
        val findings = rule.lint(compileForTest(expectedPath))
        assertThat(findings).anySatisfy { finding ->
            assertThat(finding.location.path).isEqualTo(expectedPath)
        }
    }
}
