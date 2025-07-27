package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class RangeUntilInsteadOfRangeToSpec {
    val subject = RangeUntilInsteadOfRangeTo(Config.empty)

    @Test
    @DisplayName("reports for '..'")
    fun reportsForDoubleDotsInForIterator() {
        val code = """
            fun f() {
                for (i in 0 .. 10 - 1) {}
            }
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).singleElement().hasMessage("`..` call can be replaced with `..<`")
    }

    @Test
    fun `does not report if rangeTo not used`() {
        val code = """
            fun f() {
                for (i in 0 ..< 10 - 1) {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report if upper value isn't a binary expression`() {
        val code = """
            fun f() {
                for (i in 0 .. 10) {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report if not minus one`() {
        val code = """
            fun f() {
                for (i in 0 .. 10 + 1) {}
                for (i in 0 .. 10 - 2) {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    @DisplayName("reports for '..'")
    fun reportsForDoubleDots() {
        val code = "val r = 0 .. 10 - 1"
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report binary expressions without a range operator`() {
        val code = "val sum = 1 + 2"
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports for 'rangeTo'`() {
        val code = "val r = 0.rangeTo(10 - 1)"
        val findings = subject.lint(code)
        assertThat(findings).singleElement().hasMessage("`rangeTo` call can be replaced with `..<`")
    }
}
