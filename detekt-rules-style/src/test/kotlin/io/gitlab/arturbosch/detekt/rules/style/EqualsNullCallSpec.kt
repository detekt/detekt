package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EqualsNullCallSpec {
    val subject = EqualsNullCall(Config.empty)

    @Nested
    inner class `EqualsNullCall rule` {

        @Test
        fun `reports equals call with null as parameter`() {
            val code = """
                fun x(a: String) {
                    a.equals(null)
                }
            """
            assertThat(subject.compileAndLint(code).size).isEqualTo(1)
        }

        @Test
        fun `reports nested equals call with null as parameter`() {
            val code = """
                fun x(a: String, b: String) {
                    a.equals(b.equals(null))
                }
            """
            assertThat(subject.compileAndLint(code).size).isEqualTo(1)
        }

        @Test
        fun `does not report equals call with parameter of type string`() {
            val code = """
                fun x(a: String, b: String) {
                    a.equals(b)
                }
            """
            assertThat(subject.compileAndLint(code).size).isEqualTo(0)
        }
    }
}
