package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.api.Config
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.assertThat
import dev.detekt.test.lintWithContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseRequireSpec(val env: KotlinEnvironmentContainer) {
    val subject = UseRequire(Config.empty)

    @Test
    fun `reports if a precondition throws an IllegalArgumentException`() {
        val code = """
            fun x(a: Int) {
                if (a < 0) throw IllegalArgumentException()
                println("something")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasStartSourceLocation(2, 16)
    }

    @Test
    fun `reports if a precondition throws an IllegalArgumentException as the only statement in block expression`() {
        val code = """
            fun x(a: Int) {
                if (a < 0) {
                    throw IllegalArgumentException()
                }
                println("something")
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasStartSourceLocation(3, 9)
    }

    @Test
    fun `reports if a precondition throws an IllegalArgumentException with more details`() {
        val code = """
            fun x(a: Int) {
                if (a < 0) throw IllegalArgumentException("More details")
                println("something")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasStartSourceLocation(2, 16)
    }

    @Test
    fun `reports if a precondition throws a fully qualified IllegalArgumentException`() {
        val code = """
            fun x(a: Int) {
                if (a < 0) throw java.lang.IllegalArgumentException()
                println("something")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasStartSourceLocation(2, 16)
    }

    @Test
    fun `reports if a precondition throws a fully qualified IllegalArgumentException using the kotlin type alias`() {
        val code = """
            fun x(a: Int) {
                if (a < 0) throw kotlin.IllegalArgumentException()
                println("something")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasStartSourceLocation(2, 16)
    }

    @Test
    fun `does not report if a precondition throws a different kind of exception`() {
        val code = """
            class SomeBusinessException: Exception()
            fun x(a: Int) {
                if (a < 0) throw SomeBusinessException()
                println("something")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report an issue if the exception thrown has a message and a cause`() {
        val code = """
            fun x(a: Int, cause: Exception) {
                if (a < 0) {
                    throw IllegalArgumentException("message", cause)
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report an issue if the exceptions in thrown with more than one statement in block expression`() {
        val code = """
            fun x(a: Int) {
                if (a < 0) {
                    println("bang!")
                    throw IllegalArgumentException()
                }
                println("something")
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report an issue if the exception thrown as the only action in a block`() {
        val code = """
            fun throwingFunction(): () -> Any = { throw IllegalArgumentException("message") }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report an issue if the exception thrown unconditionally`() {
        val code = """fun doThrow(): Nothing = throw IllegalArgumentException("message")"""
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report an issue if the exception thrown unconditionally in a function block`() {
        val code = """fun doThrow() { throw IllegalArgumentException("message") }"""
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report if the exception thrown has a non-String argument`() {
        val code = """
            fun test(throwable: Throwable) {
                if (throwable !is NumberFormatException) throw IllegalArgumentException(throwable)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report if the exception thrown has a String literal argument and a non-String argument`() {
        val code = """
            fun test(throwable: Throwable) {
                if (throwable !is NumberFormatException) throw IllegalArgumentException("a", throwable)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports if the exception thrown has a non-literal String argument`() {
        val code = """
            fun test(throwable: Throwable) {
                val s = ""
                if (throwable !is NumberFormatException) throw IllegalArgumentException(s)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Nested
    inner class `throw is not after a precondition` {

        @Test
        fun `does not report an issue if the exception is inside a when`() {
            val code = """
                import java.util.LinkedList

                fun whenOrThrow(item : List<*>) = when(item) {
                    is ArrayList<*> -> 1
                    is LinkedList<*> -> 2
                    else -> throw IllegalArgumentException("Not supported List type")
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report an issue if the exception is after a elvis operator`() {
            val code = """
                import java.util.LinkedList

                fun tryToCastOrThrow(list: List<*>) : LinkedList<*> {
                    val subclass = list as? LinkedList
                        ?: throw IllegalArgumentException("List is not a LinkedList")
                    return subclass
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }
}
