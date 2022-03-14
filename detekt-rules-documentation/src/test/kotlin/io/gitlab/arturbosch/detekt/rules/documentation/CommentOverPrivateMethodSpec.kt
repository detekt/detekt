package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CommentOverPrivateMethodSpec {
    val subject = CommentOverPrivateFunction()

    @Nested
    inner class `CommentOverPrivateFunction rule` {

        @Test
        fun `reports private method with a comment`() {
            val code = """
                class Test {
                    /**
                     * asdf
                     */
                    private fun f() {}
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report public method with a comment`() {
            val code = """
                /**
                 * asdf
                 */
                fun f() {}
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
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
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}
