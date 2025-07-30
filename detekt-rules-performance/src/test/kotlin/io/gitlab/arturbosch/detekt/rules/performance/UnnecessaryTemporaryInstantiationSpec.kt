package io.gitlab.arturbosch.detekt.rules.performance

import dev.detekt.api.Config
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UnnecessaryTemporaryInstantiationSpec {
    val subject = UnnecessaryTemporaryInstantiation(Config.empty)

    @Test
    fun `temporary instantiation for conversion`() {
        val code = "val i = Integer(1).toString()"
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `right conversion without instantiation`() {
        val code = "val i = Integer.toString(1)"
        assertThat(subject.lint(code)).isEmpty()
    }
}
