package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SafeCastSpec {
    val subject = SafeCast()

    @Nested
    inner class `SafeCast rule` {

        @Test
        fun `reports negated expression`() {
            val code = """
                fun test(element: Int) {
                    val cast = if (element !is Number) {
                        null
                    } else {
                        element
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports expression`() {
            val code = """
                fun test(element: Int) {
                    val cast = if (element is Number) {
                        element
                    } else {
                        null
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report wrong condition`() {
            val code = """
                fun test(element: Int) {
                    val other = 3
                    val cast = if (element == other) {
                        element
                    } else {
                        null
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report wrong else clause`() {
            val code = """
                fun test(element: Int) {
                    val cast = if (element is Number) {
                        element
                    } else {
                        String()
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}
