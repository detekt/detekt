package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class CastToNullableTypeSpec(private val env: KotlinCoreEnvironment) {
    private val subject = CastToNullableType()

    @Test
    fun `casting to nullable types`() {
        val code = """
            fun foo(a: Any?) {
                val x: String? = a as String?
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocation(2, 24)
        assertThat(findings[0]).hasMessage("Use the safe cast ('as? String') instead of 'as String?'.")
    }

    @Test
    fun `casting to nullable parent types is allowed`() {
        val code = """
            fun foo(a: String?) {
                val x = a as CharSequence?
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `safe casting`() {
        val code = """
            fun foo(a: Any?) {
                val x: String? = a as? String
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `type checking`() {
        val code = """
            fun foo(a: Any?) {
                val x = a is String?
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `cast null to nullable type is allowed`() {
        val code = """
            fun foo(a: Any?) {
                val x = null as String?
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    // https://github.com/detekt/detekt/issues/6676
    @Test
    fun `cast to same type in alias form allowed`() {
        val code = """
            typealias Alias = String

            fun test(s: String?) {
                @Suppress("USELESS_CAST") // Casts is useful to convert String to typealias.
                val a = s as Alias?
                print(a)
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `cast to parent type in alias form allowed`() {
        val code = """
            typealias Alias = CharSequence

            fun test(s: String?) {
                val a = s as Alias?
                print(a)
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `cast to different type in alias form not allowed`() {
        val code = """
            typealias Alias = String

            fun test(s: Any?) {
                val a = s as Alias?
                print(a)
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }
}
