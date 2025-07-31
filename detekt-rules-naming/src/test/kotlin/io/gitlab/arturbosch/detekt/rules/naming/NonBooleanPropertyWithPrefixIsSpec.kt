package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.test.assertThat
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class NonBooleanPropertyWithPrefixIsSpec(val env: KotlinEnvironmentContainer) {
    val subject = NonBooleanPropertyPrefixedWithIs(Config.empty)

    @Nested
    inner class `argument declarations` {
        @Test
        fun `should not detect Kotlin Boolean`() {
            val code = """data class O (var isDefault: Boolean)"""
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not detect Kotlin Boolean nullable`() {
            val code = """data class O (var isDefault: Boolean?)"""
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not detect Kotlin Boolean initialized`() {
            val code = """data class O (var isDefault: Boolean = false)"""
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not detect Java Boolean`() {
            val code = """data class O (var isDefault: java.lang.Boolean)"""
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not detect AtomicBoolean`() {
            val code = """
                import java.util.concurrent.atomic.AtomicBoolean
                data class O (var isDefault: AtomicBoolean)
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should warn about primitive types`() {
            val code = """data class O (var isDefault: Int)"""
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).hasSize(1)
            assertThat(findings.first())
                .hasMessage("Non-boolean properties shouldn't start with 'is' prefix. Actual type of isDefault: Int")
        }

        @Test
        fun `should warn about inner classes`() {
            val code = """
                data class O (var isDefault: Inner) {
                    class Inner
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not detect short names`() {
            val code = """class O (var `is`: Int)"""
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not detect titles, starting with 'is'`() {
            val code = """class O (var isengardTowerHeightInFeet: Int)"""
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `property declarations` {
        @Test
        fun `should not detect Kotlin Boolean`() {
            val code = """
                class O {
                    var isDefault = false
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not detect Kotlin Boolean property uninitialized`() {
            val code = """
                class O {
                    var isDefault: Boolean
                
                    init {
                        isDefault = true
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not detect Kotlin Boolean nullable`() {
            val code = """
                class O {
                    var isDefault: Boolean? = null
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not detect Java Boolean`() {
            val code = """
                class O {
                    var isDefault: java.lang.Boolean = java.lang.Boolean(false)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not detect Java Boolean uninitialized`() {
            val code = """
                class O {
                     var isDefault: java.lang.Boolean
                
                     init {
                         isDefault = java.lang.Boolean(false)
                     }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not detect Java Boolean nullable`() {
            val code = """
                class O {
                    var isDefault: java.lang.Boolean? = null
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not detect AtomicBoolean`() {
            val code = """
                import java.util.concurrent.atomic.AtomicBoolean
                class O {
                    var isDefault = AtomicBoolean()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should warn about primitive types in class`() {
            val code = """
                class O {
                    var isDefault: Int = 0
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should warn about inferred primitive types`() {
            val code = """
                class O {
                    var isDefault = 0
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should warn about inferred non-primitive types`() {
            val code = """
                class O {
                    var isDefault = listOf(1, 2, 3)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should warn about inner classes`() {
            val code = """
                class O {
                    var isDefault: Inner = Inner()
                
                    class Inner
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not detect short names`() {
            val code = """
                class O {
                    var `is`: Int = 0
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not detect titles, starting with 'is'`() {
            val code = """
                class O {
                    var isengardTowerHeightInFeet: Int = 500
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should warn about primitive types in function`() {
            val code = """
                fun f() {
                    var isDefault: Int = 0
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not detect boolean function`() {
            val code = """
                class O {
                    val isEnabled: () -> Boolean = { true }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not detect boolean function with parameter`() {
            val code = """
                class O {
                    val isEnabled: (String) -> Boolean = { true }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `issue regression test` {
        @Test
        fun `issue 4674 should handle unknown type as correct`() {
            val code = """
                class Test {
                    val isDebuggable get() = BuildConfig.DEBUG
                }
            """.trimIndent()

            // BuildConfig is missing in this test so we can't compile it
            val findings = subject.lintWithContext(env, code, allowCompilationErrors = true)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `issue 4675 check function reference type parameter`() {
            val code = """
                val isRemoved = suspend { null == null }
                
                fun trueFun() = true
                val isReferenceBoolean = ::trueFun
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)

            assertThat(findings).isEmpty()
        }
    }
}
