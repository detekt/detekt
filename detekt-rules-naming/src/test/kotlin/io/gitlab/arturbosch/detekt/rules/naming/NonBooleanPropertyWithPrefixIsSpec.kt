package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NonBooleanPropertyWithPrefixIsSpec : Spek({
    setupKotlinEnvironment()

    val subject by memoized { NonBooleanPropertyPrefixedWithIs() }
    val env: KotlinCoreEnvironment by memoized()

    describe("IsPropertyNaming rule") {

        context("argument declarations") {
            it("should not detect Kotlin Boolean") {
                val code = """data class O (var isDefault: Boolean)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect Kotlin Boolean nullable") {
                val code = """data class O (var isDefault: Boolean?)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect Kotlin Boolean initialized") {
                val code = """data class O (var isDefault: Boolean = false)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect Java Boolean") {
                val code = """data class O (var isDefault: java.lang.Boolean)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should warn about primitive types") {
                val code = """data class O (var isDefault: Int)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about inner classes") {
                val code = """
                    data class O (var isDefault: Inner) {
                        class Inner
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should not detect short names") {
                val code = """class O (var `is`: Int)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect titles, starting with 'is'") {
                val code = """class O (var isengardTowerHeightInFeet: Int)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }
        }

        context("property declarations") {
            it("should not detect Kotlin Boolean") {
                val code = """
                    class O {
                        var isDefault = false
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect Kotlin Boolean property uninitialized") {
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

            it("should not detect Kotlin Boolean nullable") {
                val code = """
                    class O {
                        var isDefault: Boolean? = null
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect Java Boolean") {
                val code = """
                    class O {
                        var isDefault: java.lang.Boolean = java.lang.Boolean(false)
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect Java Boolean uninitialized") {
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

            it("should not detect Java Boolean nullable") {
                val code = """
                    class O {
                        var isDefault: java.lang.Boolean? = null
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should warn about primitive types") {
                val code = """
                    class O {
                        var isDefault: Int = 0
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about inferred primitive types") {
                val code = """
                    class O {
                        var isDefault = 0
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about inferred non-primitive types") {
                val code = """
                    class O {
                        var isDefault = listOf(1, 2, 3)
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about inner classes") {
                val code = """
                    class O {
                        var isDefault: Inner = Inner()
                        
                        class Inner
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should not detect short names") {
                val code = """
                    class O {
                        var `is`: Int = 0
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect titles, starting with 'is'") {
                val code = """
                    class O {
                        var isengardTowerHeightInFeet: Int = 500
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should warn about primitive types") {
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
})
