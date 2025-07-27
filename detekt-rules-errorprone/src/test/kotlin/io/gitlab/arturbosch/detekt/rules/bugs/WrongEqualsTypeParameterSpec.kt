package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WrongEqualsTypeParameterSpec {
    private val subject = WrongEqualsTypeParameter(Config.empty)

    @Test
    fun `does not report nullable Any as parameter`() {
        val code = """
            class A {
                override fun equals(other: Any?): Boolean {
                    return super.equals(other)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports a String as parameter`() {
        val code = """
            class A {
                fun equals(other: String): Boolean {
                    return super.equals(other)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report equals() with an additional parameter`() {
        val code = """
            class A {
                fun equals(other: Any?, i: Int): Boolean {
                    return super.equals(other)
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report an overridden equals() with a different signature`() {
        val code = """
            interface I {
                fun equals(other: Any?, i: Int): Boolean
                fun equals(): Boolean
            }
            
            class A : I {
                override fun equals(other: Any?, i: Int): Boolean {
                    return super.equals(other)
                }
            
                override fun equals() = true
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report an interface declaration`() {
        val code = """
            interface I {
                fun equals(other: String)
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report top level functions`() {
        val code = """
            fun equals(other: String) {}
            fun equals(other: Any?) {}
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}
