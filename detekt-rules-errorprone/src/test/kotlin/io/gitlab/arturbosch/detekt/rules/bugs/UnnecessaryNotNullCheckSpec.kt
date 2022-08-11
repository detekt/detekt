package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessaryNotNullCheckSpec(private val env: KotlinCoreEnvironment) {
    private val subject = UnnecessaryNotNullCheck()

    @Nested
    inner class `check unnecessary not null checks` {

        @Test
        fun shouldDetectNotNullCallOnVariable() {
            val code = """
                val x = 5
                val y = requireNotNull(x)
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(18 to 35)
        }

        @Test
        fun shouldDetectNotNullCallOnVariableUsingCheckNotNull() {
            val code = """
                val x = 5
                val y = checkNotNull(x)
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(18 to 33)
        }

        @Test
        fun shouldDetectNotNullCallOnFunctionReturn() {
            val code = """
                fun foo(): Int {
                    return 5
                }
                fun bar() {
                    requireNotNull(foo())
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(48 to 69)
        }

        @Test
        fun shouldDetectWhenCallingPrimitiveJavaMethod() {
            val code = """
                fun foo() {
                    requireNotNull(System.currentTimeMillis())
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(16 to 58)
        }
    }

    @Nested
    inner class `check valid not null check usage` {

        @Test
        fun shouldIgnoreNotNullCallOnNullableVariableWithValue() {
            val code = """
                val x: Int? = 5
                val y = requireNotNull(x)
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun shouldIgnoreNotNullCallOnNullableVariableWithNull() {
            val code = """
                val x: Int? = null
                val y = requireNotNull(x)
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun shouldIgnoreNotNullCallOnNullableFunctionReturnWithValue() {
            val code = """
                fun foo(): Int? {
                    return 5
                }
                fun bar() {
                    requireNotNull(foo())
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun shouldIgnoreNotNullCallOnNullableFunctionReturnWithNull() {
            val code = """
                fun foo(): Int? {
                    return null
                }
                fun bar() {
                    requireNotNull(foo())
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun shouldIgnoreWhenCallingObjectJavaMethod() {
            val code = """
                fun foo() {
                    requireNotNull(System.getLogger())
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
}
