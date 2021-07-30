package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class BooleanPropertyNamingSpec : Spek({
    setupKotlinEnvironment()

    val subject by memoized { BooleanPropertyNaming() }
    val env: KotlinCoreEnvironment by memoized()

    describe("BooleanPropertyNaming rule") {

        context("argument declarations") {
            it("should warn about Kotlin Boolean") {
                val code = """data class Test (var default: Boolean)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about Kotlin Boolean nullable") {
                val code = """data class Test (var default: Boolean?)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about Kotlin Boolean initialized") {
                val code = """data class Test (var default: Boolean = false)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about Java Boolean") {
                val code = """data class Test (var default: java.lang.Boolean)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should not detect primitive types") {
                val code = """data class Test (var count: Int)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect titles, starting with allowed words") {
                val code = """data class Test (var isEnabled: Boolean, var hasDefault: Boolean)"""
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }
        }

        context("property declarations") {
            it("should warn about Kotlin Boolean") {
                val code = """
                    class Test {
                        var default: Boolean = true
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about Kotlin Boolean nullable") {
                val code = """
                    class Test {
                        var default: Boolean? = null
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about Kotlin Boolean initialized") {
                val code = """
                    class Test {
                        var default: Boolean = false
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about inferred boolean type") {
                val code = """
                    class Test {
                        var default = true
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about Java Boolean") {
                val code = """
                    class Test {
                        var default: java.lang.Boolean = java.lang.Boolean(true)
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).hasSize(1)
            }

            it("should not detect primitive types") {
                val code = """
                    class Test {
                        var count: Int = 0
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect titles, starting with allowed words") {
                val code = """
                    class Test {
                        var isEnabled: Boolean = true
                        var hasDefault: Boolean = true
                    }
                    """
                val findings = subject.compileAndLintWithContext(env, code)

                assertThat(findings).isEmpty()
            }

            it("should not detect titles, starting with allowed words from config") {
                val code = """
                    class Test {
                        var needReload: Boolean = true
                    }
                    """

                val config = TestConfig(mapOf(ALLOWED_PATTERN to "^(is|has|are|need)"))
                assertThat(BooleanPropertyNaming(config).compileAndLint(code))
                    .isEmpty()
            }
        }
    }
})

private const val ALLOWED_PATTERN = "allowedPattern"
