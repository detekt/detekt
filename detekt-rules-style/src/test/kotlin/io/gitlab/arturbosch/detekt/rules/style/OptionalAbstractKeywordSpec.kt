package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class OptionalAbstractKeywordSpec {
    val subject = OptionalAbstractKeyword(Config.empty)

    @Test
    fun `does not report abstract keywords on an interface`() {
        val code = "interface A {}"
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports abstract interface with abstract property`() {
        val code = "abstract interface A { abstract var x: Int }"
        assertThat(subject.compileAndLint(code)).hasSize(2)
    }

    @Test
    fun `reports abstract interface with abstract function`() {
        val code = "abstract interface A { abstract fun x() }"
        val findings = subject.compileAndLint(code)

        assertThat(findings).hasSize(2)
        assertThat(findings).hasTextLocations(0 to 8, 23 to 31)
    }

    @Test
    fun `reports nested abstract interface`() {
        val code = """
            class A {
                abstract interface B {
                    abstract fun x()
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(2)
    }

    @Test
    fun `does not report an abstract class`() {
        val code = "abstract class A { abstract fun x() }"
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report a nested abstract class function`() {
        val code = """
            interface I {
                abstract class A {
                    abstract fun dependency()
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}
