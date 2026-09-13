package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class NullableBooleanCheckSpec(val env: KotlinEnvironmentContainer) {
    val subject = NullableBooleanCheck(Config.empty)

    @Test
    fun `does not report elvis in statement by default`() {
        val code = """
            fun foo(value: Boolean?): Boolean {
                return value ?: true
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report elvis in property initializer by default`() {
        val code = """
            fun foo(value: Boolean?) {
                val isFlag = value ?: true
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports elvis in statement when onlyConditionals is false`() {
        val code = """
            fun foo(value: Boolean?): Boolean {
                return value ?: true
            }
        """.trimIndent()

        val findings = NullableBooleanCheck(TestConfig("onlyConditionals" to false))
            .lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `reports elvis in if condition`() {
        val code = """
            fun foo(value: Boolean?) {
                if (value ?: true) println("foo")
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports elvis in while condition`() {
        val code = """
            fun foo(value: Boolean?) {
                while (value ?: true) println("foo")
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports elvis in do-while condition`() {
        val code = """
            fun foo(value: Boolean?) {
                do { println("foo") } while (value ?: true)
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports elvis in when subject`() {
        val code = """
            fun foo(value: Boolean?) {
                when (value ?: true) {
                    true -> println("foo")
                    false -> println("bar")
                }
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports elvis in when condition without subject`() {
        val code = """
            fun foo(value: Boolean?) {
                when {
                    value ?: true -> println("foo")
                }
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports elvis nested in compound if condition`() {
        val code = """
            fun foo(value: Boolean?, other: Boolean) {
                if ((value ?: true) && other) println("foo")
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report for non-constant fallback`() {
        val code = """
            fun foo(value: Boolean?, fallback: Boolean): Boolean {
                return value ?: fallback
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report elvis for non-boolean statement with boolean default`() {
        val code = """
            fun foo(value: Any?): Any {
                return value ?: true
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report non-boolean elvis`() {
        val code = """
            fun foo(value: Int?): Int {
                return value ?: 0
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report non-elvis binary expression`() {
        val code = """
            fun foo(value: Boolean): Boolean {
                return value || false
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
