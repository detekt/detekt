package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EqualsNullCallSpec {
    val subject = EqualsNullCall(Config.empty)

    @Test
    fun `reports equals call with null as parameter`() {
        val code = """
            fun x(a: String) {
                a.equals(null)
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports nested equals call with null as parameter`() {
        val code = """
            fun x(a: String, b: String) {
                a.equals(b.equals(null))
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report equals call with parameter of type string`() {
        val code = """
            fun x(a: String, b: String) {
                a.equals(b)
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}
