package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UseIfInsteadOfWhenSpec {

    val subject = UseIfInsteadOfWhen()

    @Nested
    inner class `UseIfInsteadOfWhen rule` {

        @Test
        fun `reports when using two branches`() {
            val code = """
                fun function(): Boolean? {
                    val x = null
                    when (x) {
                        null -> return true
                        else -> return false
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report when using one branch`() {
            val code = """
                fun function(): Boolean? {
                    val x = null
                    when (x) {
                        else -> return false
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report when using more than two branches`() {
            val code = """
                fun function(): Boolean? {
                    val x = null
                    when (x) {
                        null -> return true
                        3 -> return null
                        else -> return false
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report when second branch is not 'else'`() {
            val code = """
                fun function(): Boolean? {
                    val x = null
                    when (x) {
                        null -> return true
                        3 -> return null
                    }
                    return false
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}
