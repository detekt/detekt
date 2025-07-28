package io.gitlab.arturbosch.detekt.rules.exceptions

import dev.detekt.api.Config
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ThrowingExceptionInMainSpec {
    val subject = ThrowingExceptionInMain(Config.empty)

    @Test
    fun `reports a runnable main function without args which throws an exception`() {
        val code = """
            fun main() { throw IllegalArgumentException() }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports a runnable main function with array args which throws an exception`() {
        val code = """
            fun main(args: Array<String>) { throw IllegalArgumentException() }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports a runnable main function with vararg args which throws an exception`() {
        val code = """
            fun main(vararg args: String) { throw IllegalArgumentException() }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports runnable main functions with @JvmStatic annotation which throw an exception`() {
        val code = """
            class A {
                companion object {
                    @JvmStatic
                    fun main(args: Array<String>) { throw IllegalArgumentException() }
                }
            }
            
            class B {
                companion object {
                    @kotlin.jvm.JvmStatic
                    fun main() { throw IllegalArgumentException() }
                }
            }
            
            object O {
                @JvmStatic
                fun main(args: Array<String>) { throw IllegalArgumentException() }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(3)
    }

    @Test
    fun `does not report top level main functions with a wrong signature`() {
        val code = """
            private fun main(args: Array<String>) { throw IllegalArgumentException() }
            private fun main() { throw IllegalArgumentException() }
            fun mai() { throw IllegalArgumentException() }
            fun main(args: String) { throw IllegalArgumentException() }
            fun main(args: Array<String>, i: Int) { throw IllegalArgumentException() }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report top level main functions which throw no exception`() {
        val code = """
            fun main(args: Array<String>) { }
            fun main() { }
            fun mai() { }
            fun main(args: String) { }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report top level main functions with expression body which throw no exception`() {
        val code = """
            fun main(args: Array<String>) = ""
            fun main() = Unit
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report main functions with no @JvmStatic annotation inside a class`() {
        val code = """
            class A {
                fun main(args: Array<String>) { throw IllegalArgumentException() }
                
                companion object {
                    fun main(args: Array<String>) { throw IllegalArgumentException() }
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}
