package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Test

class OptionalAbstractKeywordSpec {
    val subject = OptionalAbstractKeyword(Config.Empty)

    @Test
    fun `does not report abstract keywords on an interface`() {
        val code = "interface A {}"
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports abstract interface with abstract property`() {
        val code = "abstract interface A { abstract var x: Int }"
        assertThat(subject.lint(code)).hasSize(2)
    }

    @Test
    fun `reports abstract interface with abstract function`() {
        val code = "abstract interface A { abstract fun x() }"
        val findings = subject.lint(code)

        assertThat(findings).satisfiesExactlyInAnyOrder(
            { assertThat(it).hasTextLocation(0 to 8) },
            { assertThat(it).hasTextLocation(23 to 31) },
        )
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
        assertThat(subject.lint(code)).hasSize(2)
    }

    @Test
    fun `does not report an abstract class`() {
        val code = "abstract class A { abstract fun x() }"
        assertThat(subject.lint(code)).isEmpty()
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
        assertThat(subject.lint(code)).isEmpty()
    }
}
