package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class CastToNullableTypeSpec {
    private val subject = CastToNullableType()

    @Test
    fun `casting to nullable types`() {
        val code = """
            fun foo(a: Any?) {
                val x: String? = a as String?
            } 
        """.trimIndent()
        val findings = subject.compileAndLint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocation(2, 24)
        assertThat(findings[0]).hasMessage("Use the safe cast ('as? String') instead of 'as String?'.")
    }

    @Test
    fun `safe casting`() {
        val code = """
            fun foo(a: Any?) {
                val x: String? = a as? String
            } 
        """.trimIndent()
        val findings = subject.compileAndLint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `type checking`() {
        val code = """
            fun foo(a: Any?) {
                val x = a is String?
            } 
        """.trimIndent()
        val findings = subject.compileAndLint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `cast null to nullable type is allowed`() {
        val code = """
            fun foo(a: Any?) {
                val x = null as String?
            }
        """.trimIndent()
        val findings = subject.compileAndLint(code)
        assertThat(findings).isEmpty()
    }
}
