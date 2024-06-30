package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ErrorUsageWithThrowableSpec(private val env: KotlinCoreEnvironment) {
    val subject = ErrorUsageWithThrowable(Config.empty)

    @Test
    fun `reports error getting called with Exception instance`() {
        val code = """
            fun foo() {
                try {
                    // ... some code
                } catch(err: RuntimeException) {
                    // some addition handling
                    error(err)
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0]).hasSourceLocation(6, 15)
    }

    @Test
    fun `does not report error getting called with Exception message`() {
        val code = """
            fun foo() {
                try {
                    // ... some code
                } catch(err: RuntimeException) {
                    // some addition handling
                    error(err.message.orEmpty())
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report error getting called with Exception stacktrace`() {
        val code = """
            fun foo() {
                try {
                    // ... some code
                } catch(exception: RuntimeException) {
                    // some addition handling
                    error(exception.stackTraceToString())
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for custom error fun getting called with error instance`() {
        val code = """
            fun error(any: Any) = println("error called with ${'$'}any")

            fun foo() {
                try {
                    // ... some code
                } catch(e: RuntimeException) {
                    // some addition handling
                    error(e)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does report with java throwable types`() {
        val code = """
            import java.lang.System

            fun foo() {
                val illegalStateExceptionJava = java.lang.IllegalStateException("")
                val throwableJava = java.lang.Throwable("")
                when(System.currentTimeMillis() % 4) {
                    0L -> error(illegalStateExceptionJava)
                    1L -> error(throwableJava)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
    }

    @Test
    fun `does report with kotlin throwable types`() {
        val code = """
            import java.lang.System

            fun foo() {
                val illegalStateExceptionJava = kotlin.IllegalStateException("")
                val throwableJava = kotlin.Throwable("")
                when(System.currentTimeMillis() % 4) {
                    0L -> error(illegalStateExceptionJava)
                    1L -> error(throwableJava)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
    }
}
