package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseRequireSpec(val env: KotlinCoreEnvironment) {
    val subject = UseRequire(Config.empty)

    @Test
    fun `reports if a precondition throws an IllegalArgumentException`() {
        val code = """
            fun x(a: Int) {
                if (a < 0) throw IllegalArgumentException()
                doSomething()
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasStartSourceLocation(2, 16)
    }

    @Test
    fun `reports if a precondition throws an IllegalArgumentException as the only statement in block expression`() {
        val code = """
            fun x(a: Int) {
                if (a < 0) {
                    throw IllegalArgumentException()
                }
                doSomething()
            }
        """.trimIndent()

        assertThat(subject.lint(code)).hasStartSourceLocation(3, 9)
    }

    @Test
    fun `reports if a precondition throws an IllegalArgumentException with more details`() {
        val code = """
            fun x(a: Int) {
                if (a < 0) throw IllegalArgumentException("More details")
                doSomething()
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasStartSourceLocation(2, 16)
    }

    @Test
    fun `reports if a precondition throws a fully qualified IllegalArgumentException`() {
        val code = """
            fun x(a: Int) {
                if (a < 0) throw java.lang.IllegalArgumentException()
                doSomething()
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasStartSourceLocation(2, 16)
    }

    @Test
    fun `reports if a precondition throws a fully qualified IllegalArgumentException using the kotlin type alias`() {
        val code = """
            fun x(a: Int) {
                if (a < 0) throw kotlin.IllegalArgumentException()
                doSomething()
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasStartSourceLocation(2, 16)
    }

    @Test
    fun `does not report if a precondition throws a different kind of exception`() {
        val code = """
            fun x(a: Int) {
                if (a < 0) throw SomeBusinessException()
                doSomething()
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report an issue if the exception thrown has a message and a cause`() {
        val code = """
            private fun x(a: Int): Nothing {
                doSomething()
                throw IllegalArgumentException("message", cause)
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report an issue if the exceptions in thrown with more than one statement in block expression`() {
        val code = """
            fun x(a: Int) {
                if (a < 0) {
                    println("bang!")
                    throw IllegalArgumentException()
                }
                doSomething()
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report an issue if the exception thrown as the only action in a block`() {
        val code = """
            fun unsafeRunSync(): A =
                foo.fold({ throw IllegalArgumentException("message") }, ::identity)
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report an issue if the exception thrown unconditionally`() {
        val code = """fun doThrow() = throw IllegalArgumentException("message")"""
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report an issue if the exception thrown unconditionally in a function block`() {
        val code = """fun doThrow() { throw IllegalArgumentException("message") }"""
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report if the exception thrown has a non-String argument`() {
        val code = """
            fun test(throwable: Throwable) {
                if (throwable !is NumberFormatException) throw IllegalArgumentException(throwable)
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report if the exception thrown has a String literal argument and a non-String argument`() {
        val code = """
            fun test(throwable: Throwable) {
                if (throwable !is NumberFormatException) throw IllegalArgumentException("a", throwable)
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report if the exception thrown has a non-String literal argument`() {
        val code = """
            fun test(throwable: Throwable) {
                val s = ""
                if (throwable !is NumberFormatException) throw IllegalArgumentException(s)
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Nested
    inner class `with binding context` {

        @Test
        fun `does not report if the exception thrown has a non-String argument`() {
            val code = """
                fun test(throwable: Throwable) {
                    if (throwable !is NumberFormatException) throw IllegalArgumentException(throwable)
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report if the exception thrown has a String literal argument and a non-String argument`() {
            val code = """
                fun test(throwable: Throwable) {
                    if (throwable !is NumberFormatException) throw IllegalArgumentException("a", throwable)
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports if the exception thrown has a non-String literal argument`() {
            val code = """
                fun test(throwable: Throwable) {
                    val s = ""
                    if (throwable !is NumberFormatException) throw IllegalArgumentException(s)
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports if the exception thrown has a String literal argument`() {
            val code = """
                fun test(throwable: Throwable) {
                    if (throwable !is NumberFormatException) throw IllegalArgumentException("a")
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }

    @Nested
    inner class `throw is not after a precondition` {

        @Test
        fun `does not report an issue if the exception is inside a when`() {
            val code = """
                fun whenOrThrow(item : List<*>) = when(item) {
                    is ArrayList<*> -> 1
                    is LinkedList<*> -> 2
                    else -> throw IllegalArgumentException("Not supported List type")
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report an issue if the exception is after a block`() {
            val code = """
                fun doSomethingOrThrow(test: Int): Int {
                    var index = 0
                    repeat(test){
                        if (Math.random() == 1.0) {
                            return it
                        }
                    }
                    throw IllegalArgumentException("Test was too big")
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report an issue if the exception is after a elvis operator`() {
            val code = """
                fun tryToCastOrThrow(list: List<*>) : LinkedList<*> {
                    val subclass = list as? LinkedList
                        ?: throw IllegalArgumentException("List is not a LinkedList")
                    return subclass
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }
}
