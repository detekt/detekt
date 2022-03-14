package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MultilineLambdaItParameterSpec : Spek({
    setupKotlinEnvironment()
    val subject by memoized { MultilineLambdaItParameter(Config.empty) }
    val env: KotlinCoreEnvironment by memoized()

    describe("MultilineLambdaItParameter rule") {
        context("single parameter, multiline lambda with multiple statements") {
            it("reports when parameter name is implicit `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let {
                        listOf(it)
                        println(it)
                    }
                }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            it("reports when parameter name is explicit `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { it ->
                        listOf(it)
                        println(it)
                    }
                }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            it("does not report when parameter name is explicit and not `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { param ->
                        listOf(param)
                        println(param)
                    }
                }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            it("does not report when lambda has no implicit parameter references") {
                val code = """
                fun foo(f: (Int) -> Unit) {}
                fun main() {
                    foo {
                        println(1)
                        println(2)
                        val it = 3
                        println(it)
                    }
                }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        context("single parameter, multiline lambda with a single statement") {
            it("does not report when parameter name is an implicit `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { 
                        listOf(it)
                    }
                }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            it("does not report when parameter name is an explicit `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { it ->
                        listOf(it)
                    }
                }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
            it("does not report when parameter name is explicit and not `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { param ->
                        listOf(param)
                    }
                }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        context("single parameter, single-line lambda") {
            it("does not report when parameter name is an implicit `it` with type resolution") {
                val code = """
                fun f() {
                    val digits = 1234.let { listOf(it) }
                }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            it("does not report when parameter name is an implicit `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { listOf(it) }
                }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            it("does not report when parameter name is an explicit `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { it -> listOf(it) }
                }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            it("does not report when parameter name is explicit and not `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { param -> listOf(param) }
                }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        context("multiple parameters, multiline lambda") {
            it("reports when one of the explicit parameters is an `it`") {
                val code = """
                fun f() {
                    val flat = listOf(listOf(1), listOf(2)).mapIndexed { index, it ->
                        println(it)
                        it + index 
                    }
                }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            it("does not report when none of the explicit parameters is an `it`") {
                val code = """
                fun f() {
                    val lambda = { item: Int, that: String -> 
                        println(item)
                        item.toString() + that 
                    }
                }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        context("no parameter, multiline lambda with multiple statements") {
            it("does not report when there is no parameter") {
                val code = """
                fun f() {
                    val string = StringBuilder().apply {
                        append("a")
                        append("b")
                    }
                }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }
    }
})
