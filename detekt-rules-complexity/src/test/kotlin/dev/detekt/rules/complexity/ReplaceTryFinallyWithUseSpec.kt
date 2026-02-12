package dev.detekt.rules.complexity

import dev.detekt.api.Config
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ReplaceTryFinallyWithUseSpec(val env: KotlinEnvironmentContainer) {
    val subject = ReplaceTryFinallyWithUse(Config.empty)

    @Test
    fun `reports close in try-catch-finally block`() {
        val code = """
                class ExampleCloseable: AutoCloseable {
                    override fun close() { }
                }
            
                val closeable = ExampleCloseable()
                fun test() {
                    try {
            
                    } catch (e: Exception) {
                    
                    } finally {
                        closeable.close()
                    }
                }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports close in try-finally block`() {
        val code = """
                class ExampleCloseable: AutoCloseable {
                    override fun close() { }
                }
            
                val closeable = ExampleCloseable()
                fun test() {
                    try {
            
                    } finally {
                        closeable.close()
                    }
                }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report close overload in finally block`() {
        val code = """
                class ExampleCloseable: AutoCloseable {
                    override fun close() { }
                    fun close(test: String) { }
                }
            
                val closeable = ExampleCloseable()
                fun test() {
                    try {
            
                    } finally {
                        closeable.close("")
                    }
                }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report with empty block`() {
        val code = """
                class ExampleCloseable: AutoCloseable {
                    override fun close() { }
                }
            
                val closeable = ExampleCloseable()
                fun test() {
                    try {
            
                    } finally {
                        // empty
                    }
                }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report with no close calls`() {
        val code = """
                class ExampleCloseable: AutoCloseable {
                    override fun close() { }
                    fun test() { }
                }
            
                val closeable = ExampleCloseable()
                fun test() {
                    try {
            
                    } finally {
                        closeable.test()
                    }
                }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `only reports when receiver is AutoCloseable`() {
        val code = """
                interface NotCloseableInterface {
                    fun close()
                }

                class NotCloseable : NotCloseableInterface {
                    override fun close() {}
                }
            
                val closeable = NotCloseable()
                fun test() {
                    try {
            
                    } finally {
                        closeable.close()
                    }
                }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
