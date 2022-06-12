package io.gitlab.arturbosch.detekt.rules.bugs

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IteratorHasNextCallsNextMethodSpec {
    private val subject = IteratorHasNextCallsNextMethod()

    @Test
    fun `reports wrong iterator implementation`() {
        val path = resourceAsPath("IteratorImplPositive.kt")
        assertThat(subject.lint(path)).hasSize(4)
    }

    @Test
    fun `does not report correct iterator implementations`() {
        val path = resourceAsPath("IteratorImplNegative.kt")
        assertThat(subject.lint(path)).isEmpty()
    }
}
