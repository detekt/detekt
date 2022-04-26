package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class NewLineAtEndOfFileSpec {

    val subject = NewLineAtEndOfFile()

    @Test
    fun `should not flag a kt file containing new line at the end`() {
        val code = "class Test\n\n" // we need double '\n' because .lint() applies .trimIndent() which removes one
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should flag a kt file not containing new line at the end`() {
        val code = "class Test"
        assertThat(subject.compileAndLint(code)).hasSize(1)
            .hasSourceLocation(1, 11)
    }

    @Test
    fun `should not flag an empty kt file`() {
        val code = ""
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}
