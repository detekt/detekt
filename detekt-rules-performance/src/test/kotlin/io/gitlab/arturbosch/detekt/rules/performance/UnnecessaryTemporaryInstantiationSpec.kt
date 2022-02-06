package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UnnecessaryTemporaryInstantiationSpec {
    val subject = UnnecessaryTemporaryInstantiation()

    @Nested
    inner class `UnnecessaryTemporaryInstantiation rule` {

        @Test
        fun `temporary instantiation for conversion`() {
            val code = "val i = Integer(1).toString()"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `right conversion without instantiation`() {
            val code = "val i = Integer.toString(1)"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}
