package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class CastNullableToNonNullableTypeSpec(private val env: KotlinCoreEnvironment) {
    private val subject = CastNullableToNonNullableType()

    @Test
    fun `reports casting Nullable type to NonNullable type`() {
        val code = """
            fun foo(bar: Any?) {
                val x = bar as String
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocation(2, 17)
        assertThat(findings[0]).hasMessage(
            "Use separate `null` assertion and type cast like " +
                "('(bar ?: error(\"null assertion message\")) as String') instead of 'bar as String'."
        )
    }

    @Test
    fun `reports casting Nullable value returned from a function call to NonNullable type`() {
        val code = """
            fun foo(bar: Any?) {
                bar() as Int
            }

            fun bar(): Int? {
                return null
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocation(2, 11)
        assertThat(findings[0]).hasMessage(
            "Use separate `null` assertion and type cast like " +
                "('(bar() ?: error(\"null assertion message\")) as Int') instead of 'bar() as Int'."
        )
    }

    @Test
    fun `reports casting of platform type to NonNullable type`() {
        val code = """
            class Foo {
                fun test() {
                    // getSimpleName() is not annotated with nullability information in the JDK, so compiler treats
                    // it as a platform type with unknown nullability.
                    val y = javaClass.simpleName as String
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocation(5, 38)
        assertThat(findings[0]).hasMessage(
            "Use separate `null` assertion and type cast like " +
                "('(javaClass.simpleName ?: error(\"null assertion message\")) as String') instead of " +
                "'javaClass.simpleName as String'."
        )
    }

    @Test
    fun `does not report casting of Nullable type to NonNullable expression with assertion to NonNullable type`() {
        val code = """
            fun foo(bar: Any?) {
                val x = (bar ?: error("null assertion message")) as String
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report casting of Nullable type to NonNullable expression with !! assertion to NonNullable type`() {
        val code = """
            fun foo(bar: Any?) {
                val x = bar!! as String
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report casting of Nullable type to NonNullable smart casted variable to NonNullable type`() {
        val code = """
            fun foo(bar: Any?) {
                val x = bar?.let { bar as String }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report casting of NonNullable type to NonNullable type`() {
        val code = """
            fun foo(bar: Any?) {
                val x = bar as String?
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report safe casting of Nullable type to NonNullable type`() {
        val code = """
            fun foo(bar: Any?) {
                val x = bar as? String
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report as compile error will happen when null to NonNullable type`() {
        val code = """
            fun foo(bar: Any?) {
                val x = null as String
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }
}
