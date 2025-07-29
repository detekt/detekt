package io.gitlab.arturbosch.detekt.rules.documentation

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CommentOverPrivateMethodSpec {
    val subject = CommentOverPrivateFunction(Config.empty)

    @Test
    fun `reports private method with a comment`() {
        val code = """
            class Test {
                /**
                 * asdf
                 */
                private fun f() {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report public method with a comment`() {
        val code = """
            /**
             * asdf
             */
            fun f() {}
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report public method in a class with a comment`() {
        val code = """
            class Test {
                /**
                 * asdf
                 */
                fun f() {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}
