package io.gitlab.arturbosch.detekt.rules.bugs

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import dev.detekt.test.assertThat
import dev.detekt.test.lintWithContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessaryNotNullCheckSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = UnnecessaryNotNullCheck(Config.empty)

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
        fun shouldDetectWhenCallingDefinitelyNonNullableGenericFunction() {
            val code = """
                fun <T> foo(x: T & Any): T & Any {
                    return x
                }
                fun bar() {
                    requireNotNull(foo(5))
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(66 to 88)
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

        @Test
        fun shouldDetectAfterNullCheck() {
            val code = """
                fun foo(x: Int?) {
                    if (x != null) {
                        requireNotNull(x)
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun shouldDetectAfterTypeCheck() {
            val code = """
                fun bar(x: Any?) {
                    if (x is String) {
                        requireNotNull(x)
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
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
        fun shouldIgnoreWhenCallingNullableGenericFunction() {
            val code = """
                fun <T> foo(x: T): T {
                    return x
                }
                fun bar() {
                    requireNotNull(foo<Int?>(5))
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun shouldIgnoreWhenCallingObjectJavaMethod() {
            val code = """
                fun foo() {
                    requireNotNull(System.getLogger(""))
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
}
