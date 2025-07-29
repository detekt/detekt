package dev.detekt.rules.documentation

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CommentOverPrivatePropertiesSpec {
    private val subject = CommentOverPrivateProperty(Config.empty)

    @Test
    fun `reports private property with a comment`() {
        val code = """
            /**
             * asdf
             */
            private val v = 1
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report public property with a comment`() {
        val code = """
            /**
             * asdf
             */
            val v = 1
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports private property in class with a comment`() {
        val code = """
                class Test {
                /**
                 * asdf
                 */
                private val v = 1
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report public property in class with a comment`() {
        val code = """
            class Test {
                /**
                 * asdf
                 */
                val v = 1
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}
