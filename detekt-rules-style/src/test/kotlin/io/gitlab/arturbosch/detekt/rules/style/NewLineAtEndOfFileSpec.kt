package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Test

class NewLineAtEndOfFileSpec {

    val subject = NewLineAtEndOfFile(Config.empty)

    @Test
    fun `should not flag a kt file containing new line at the end`() {
        val code = "class Test\n"
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `should flag a kt file not containing new line at the end`() {
        val code = "class Test"
        assertThat(subject.lint(code)).hasSize(1)
            .hasStartSourceLocation(1, 11)
    }

    @Test
    fun `should not flag an empty kt file`() {
        val code = ""
        assertThat(subject.lint(code)).isEmpty()
    }
}
