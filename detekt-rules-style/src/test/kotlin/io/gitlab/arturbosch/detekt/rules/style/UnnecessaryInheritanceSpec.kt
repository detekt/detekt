package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UnnecessaryInheritanceSpec {
    val subject = UnnecessaryInheritance(Config.empty)

    @Test
    fun `has unnecessary super type declarations`() {
        val findings = subject.compileAndLint(
            """
                class A : Any()
                class B : Object()
            """.trimIndent()
        )
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `has no unnecessary super type declarations`() {
        val findings = subject.lint("class C : An()")
        assertThat(findings).isEmpty()
    }
}
