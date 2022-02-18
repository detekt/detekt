package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class NonBooleanPropertyWithPrefixIsSpec(val env: KotlinCoreEnvironment) {
    val subject = NonBooleanPropertyPrefixedWithIs()

    @Nested
    inner class `IsPropertyNaming rule` {

        @Nested
        inner class `argument declarations` {
            @Test
            fun `should not detect Kotlin Boolean`() {
                val code = """data class O (var isDefault: Boolean)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            @Test
            fun `should not detect Kotlin Boolean nullable`() {
                val code = """data class O (var isDefault: Boolean?)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            @Test
            fun `should not detect Kotlin Boolean initialized`() {
                val code = """data class O (var isDefault: Boolean = false)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            @Test
            fun `should not detect Java Boolean`() {
                val code = """data class O (var isDefault: java.lang.Boolean)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            @Test
            fun `should warn about primitive types`() {
                val code = """data class O (var isDefault: Int)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            @Test
            fun `should warn about inner classes`() {
                val code = """
                    data class O (var isDefault: Inner) {
                        class Inner
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            @Test
            fun `should not detect short names`() {
                val code = """class O (var `is`: Int)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            @Test
            fun `should not detect titles, starting with 'is'`() {
                val code = """class O (var isengardTowerHeightInFeet: Int)"""
                val findings = subject.compileAndLintWithContext(env, code)

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
                    """
                val findings = subject.compileAndLintWithContext(env, code)

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
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            @Test
            fun `should not detect Kotlin Boolean nullable`() {
                val code = """
                    class O {
                        var isDefault: Boolean? = null
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            @Test
            fun `should not detect Java Boolean`() {
                val code = """
                    class O {
                        var isDefault: java.lang.Boolean = java.lang.Boolean(false)
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

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
                   """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            @Test
            fun `should not detect Java Boolean nullable`() {
                val code = """
                    class O {
                        var isDefault: java.lang.Boolean? = null
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            @Test
            fun `should warn about primitive types in class`() {
                val code = """
                    class O {
                        var isDefault: Int = 0
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            @Test
            fun `should warn about inferred primitive types`() {
                val code = """
                    class O {
                        var isDefault = 0
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            @Test
            fun `should warn about inferred non-primitive types`() {
                val code = """
                    class O {
                        var isDefault = listOf(1, 2, 3)
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            @Test
            fun `should warn about inner classes`() {
                val code = """
                    class O {
                        var isDefault: Inner = Inner()
                        
                        class Inner
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            @Test
            fun `should not detect short names`() {
                val code = """
                    class O {
                        var `is`: Int = 0
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            @Test
            fun `should not detect titles, starting with 'is'`() {
                val code = """
                    class O {
                        var isengardTowerHeightInFeet: Int = 500
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            @Test
            fun `should warn about primitive types in function`() {
                val code = """
                    fun f() {
                        var isDefault: Int = 0
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }
        }
    }
}
