package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseCheckOrErrorSpec(val env: KotlinCoreEnvironment) {
    val subject = UseCheckOrError(Config.empty)

    @Test
    fun `reports if a an IllegalStateException is thrown`() {
        val code = """
            fun x(a: Int) {
                println("something")
                if (a < 0) throw IllegalStateException()
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasStartSourceLocation(3, 16)
    }

    @Test
    fun `reports if a an IllegalStateException is thrown conditionally in a block`() {
        val code = """
            fun x(a: Int) {
                println("something")
                if (a < 0) {
                    throw IllegalStateException()
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasStartSourceLocation(4, 9)
    }

    @Test
    fun `reports if a an IllegalStateException is thrown with an error message`() {
        val code = """
            fun x(a: Int) {
                println("something")
                if (a < 0) throw IllegalStateException("More details")
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasStartSourceLocation(3, 16)
    }

    @Test
    fun `reports if a an IllegalStateException is thrown as default case of a when expression`() {
        val code = """
            fun x(a: Int) =
                when (a) {
                    1 -> println("something")
                    else -> throw IllegalStateException()
                }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasStartSourceLocation(4, 17)
    }

    @Test
    fun `reports if an IllegalStateException is thrown by its fully qualified name`() {
        val code = """
            fun x(a: Int) {
                println("something")
                if (a < 0) throw java.lang.IllegalStateException()
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasStartSourceLocation(3, 16)
    }

    @Test
    fun `reports if an IllegalStateException is thrown by its fully qualified name using the kotlin type alias`() {
        val code = """
            fun x(a: Int) {
                println("something")
                if (a < 0) throw kotlin.IllegalStateException()
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasStartSourceLocation(3, 16)
    }

    @Test
    fun `does not report if any other kind of exception is thrown`() {
        val code = """
            class SomeBusinessException: Exception()

            fun x(a: Int) {
                println("something")
                if (a < 0) throw SomeBusinessException()
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report an issue if the exception thrown has a message and a cause`() {
        val code = """
            fun missing(cause: Exception): Nothing {
                if  (cause != null) {
                    throw IllegalStateException("message", cause)
                }
                throw Exception()
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report an issue if the exception thrown as the only action in a block`() {
        val code = """
            fun unsafeRunSync(): A =
                unsafeRunTimed(Duration.INFINITE)
                    .fold({ throw IllegalStateException("message") }, ::identity)
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports an issue if the exception thrown as the only action in a function`() {
        val code = """fun doThrow(): Nothing = throw IllegalStateException("message")"""
        assertThat(subject.compileAndLintWithContext(env, code)).hasStartSourceLocation(1, 26)
    }

    @Test
    fun `reports an issue if the exception thrown as the only action in a function block`() {
        val code = """fun doThrow(): Nothing { throw IllegalStateException("message") }"""
        assertThat(subject.compileAndLintWithContext(env, code)).hasStartSourceLocation(1, 26)
    }

    @Test
    fun `does not report if the exception thrown has a non-String argument`() {
        val code = """
            fun test(throwable: Throwable) {
                when(throwable) {
                    is NumberFormatException -> println("a")
                    else -> throw IllegalStateException(throwable)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report if the exception thrown has a String literal argument and a non-String argument`() {
        val code = """
            fun test(throwable: Throwable) {
                when(throwable) {
                    is NumberFormatException -> println("a")
                    else -> throw IllegalStateException("b", throwable)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports if the exception thrown has a non-literal String argument`() {
        val code = """
            fun test(throwable: Throwable) {
                when(throwable) {
                    is NumberFormatException -> println("a")
                    else -> throw IllegalStateException(throwable.toString())
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports if the exception thrown has a string literal argument`() {
        val code = """
            fun test(throwable: Throwable) {
                when(throwable) {
                    is NumberFormatException -> println("a")
                    else -> throw IllegalStateException("b")
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }
}
