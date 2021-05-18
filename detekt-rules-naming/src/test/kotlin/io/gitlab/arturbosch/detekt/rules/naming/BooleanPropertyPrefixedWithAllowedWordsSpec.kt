package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class BooleanPropertyPrefixedWithAllowedWordsSpec : Spek({
    setupKotlinEnvironment()

    val subject by memoized { BooleanPropertyPrefixedWithAllowedWords() }
    val env: KotlinCoreEnvironment by memoized()

    describe("IsPropertyNaming rule") {

        context("argument declarations") {
            it("should warn about Kotlin Boolean") {
                val code = """data class O (var default: Boolean)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about Kotlin Boolean nullable") {
                val code = """data class O (var default: Boolean?)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about Kotlin Boolean initialized") {
                val code = """data class O (var default: Boolean = false)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about Java Boolean") {
                val code = """data class O (var default: java.lang.Boolean)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should not detect primitive types") {
                val code = """data class O (var count: Int)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect 'predicate' name") {
                val code = """data class O (var predicate: Boolean)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect 'condition' name") {
                val code = """data class O (var condition: Boolean)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect 'it' name") {
                val code = """data class O (var it: Boolean)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect '_' name") {
                val code = """data class O (var _: Boolean)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect titles, starting with allowed words") {
                val code = """data class O (var isEnabled: Boolean, var hasDefault: Boolean)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }
        }

        context("property declarations") {
            it("should warn about Kotlin Boolean") {
                val code = """
                    class O {
                        var default: Boolean = true
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about Kotlin Boolean nullable") {
                val code = """
                    class O {
                        var default: Boolean? = null
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about Kotlin Boolean initialized") {
                val code = """
                    class O {
                        var default: Boolean = false
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about inferred boolean type") {
                val code = """
                    class O {
                        var default = true
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about Java Boolean") {
                val code = """
                    class O {
                        var default: java.lang.Boolean = java.lang.Boolean(true)
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should not detect primitive types") {
                val code = """
                    class O {
                        var count: Int
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect constants") {
                val code = """
                    class O {
                        const var DEFAULT: Boolean
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect titles, starting with allowed words") {
                val code = """
                    class O {
                        var isEnabled: Boolean = true
                        var hasDefault: Boolean = true
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }
        }
    }
})
