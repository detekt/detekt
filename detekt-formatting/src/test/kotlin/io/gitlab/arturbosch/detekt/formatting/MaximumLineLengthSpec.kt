package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.formatting.wrappers.MaximumLineLength
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class MaximumLineLengthSpec {

    private lateinit var subject: MaximumLineLength

    @BeforeEach
    fun createSubject() {
        subject = MaximumLineLength(TestConfig(MAX_LINE_LENGTH to "30"))
    }

    @Nested
    inner class `a single function` {

        val code = """
            package home.test
            fun f() { /* 123456789012345678901234567890 */ }
        """.trimIndent()

        @Test
        fun `reports line which exceeds the threshold`() {
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `reports issues with the filename and package as signature`() {
            val finding = subject.lint(
                code,
                Path("home", "test", "Test.kt").toString()
            ).first()

            assertThat(finding.entity.signature).isEqualTo("home.test.Test.kt:2")
        }

        @Test
        fun `does not report line which does not exceed the threshold`() {
            val config = TestConfig(MAX_LINE_LENGTH to code.length)
            assertThat(MaximumLineLength(config).lint(code)).isEmpty()
        }
    }

    @Test
    fun `does not report line which does not exceed the threshold`() {
        val code = "val a = 1"
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports correct line numbers`() {
        val findings = subject.lint(longLines)

        // Note that KtLint's MaximumLineLength rule, in contrast to detekt's MaxLineLength rule, does not report
        // exceeded lines in block comments.
        assertThat(findings).hasSize(2)

        assertThat(findings[0].entity.location.source.line).isEqualTo(8)
        assertThat(findings[1].entity.location.source.line).isEqualTo(14)
    }

    @Test
    fun `does not report back ticked line which exceeds the threshold`() {
        val code = """
            package home.test
            fun `this is a test method that has more than 30 characters inside the back ticks`() {
            }
        """.trimIndent()
        val findings = MaximumLineLength(
            TestConfig(
                MAX_LINE_LENGTH to "30",
                "ignoreBackTickedIdentifier" to "true"
            )
        ).lint(code)
        assertThat(findings).isEmpty()
    }
}

const val MAX_LINE_LENGTH = "maxLineLength"
