package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.wrappers.ChainWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.NoLineBreakBeforeAssignment
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import dev.detekt.test.location
import dev.detekt.test.utils.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.absolute

class KtlintRuleSpec {

    private lateinit var subject: NoLineBreakBeforeAssignment

    @BeforeEach
    fun createSubject() {
        subject = NoLineBreakBeforeAssignment(Config.Empty)
    }

    @Nested
    inner class `ktlint rules can be suppressed` {

        @Test
        fun `support suppression on node level`() {
            val findings = subject.lint(
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
    inner class `ktlint rules have a signature` {

        @Test
        fun `in a file without package name`() {
            val findings = subject.lint(
                """
                    fun main()
                    = Unit
                """.trimIndent()
            )

            assertThat(findings).singleElement()
                .hasTextLocation(10 to 11)
        }

        @Test
        fun `with a file with package name`() {
            val findings = subject.lint(
                """
                    package test.test.test
                    fun main()
                    = Unit
                """.trimIndent()
            )

            assertThat(findings).singleElement()
                .hasTextLocation(33 to 34)
        }
    }

    @Test
    fun `#3063_ ktlint issues have an absolute path`() {
        val expectedPath = Path("src/test/resources/configTests/chain-wrapping-before.kt").absolute()

        val rule = ChainWrapping(Config.Empty)
        val findings = rule.lint(compileForTest(expectedPath))
        assertThat(findings).anySatisfy { finding ->
            assertThat(finding.location.path).isEqualTo(expectedPath)
        }
    }
}
