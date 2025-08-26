package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
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
        assertThat(subject.lint(code)).singleElement()
            .hasStartSourceLocation(1, 11)
    }

    @Test
    fun `should not flag an empty kt file`() {
        val code = ""
        assertThat(subject.lint(code)).isEmpty()
    }
}
