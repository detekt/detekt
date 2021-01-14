package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MultilineLambdaItParameterSpec : Spek({
    val subject by memoized { MultilineLambdaItParameter(Config.empty) }

    describe("MultilineLambdaItParameter rule") {
        context("single parameter, multiline lambda with multiple statements") {
            it("reports when parameter name is implicit `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { 
                        listOf(it)
                        println(it)
                    }
                }"""
                val findings = subject.compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
            it("reports when parameter name is explicit `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { it ->
                        listOf(it)
                        println(it)
                    }
                }"""
                val findings = subject.compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
            it("does not report when parameter name is explicit and not `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { param ->
                        listOf(param)
                        println(param)
                    }
                }"""
                val findings = subject.compileAndLint(code)
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
                }"""
                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
            it("does not report when parameter name is an explicit `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { it ->
                        listOf(it)
                    }
                }"""
                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
            it("does not report when parameter name is explicit and not `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { param ->
                        listOf(param)
                    }
                }"""
                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("single parameter, single-line lambda") {
            it("does not report when parameter name is an implicit `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { listOf(it) }
                }"""
                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
            it("does not report when parameter name is an explicit `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { it -> listOf(it) }
                }"""
                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
            it("does not report when parameter name is explicit and not `it`") {
                val code = """
                fun f() {
                    val digits = 1234.let { param -> listOf(param) }
                }"""
                val findings = subject.compileAndLint(code)
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
                }"""
                val findings = subject.compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
            it("does not report when none of the explicit parameters is an `it`") {
                val code = """
                fun f() {
                    val lambda = { item: Int, that: String -> 
                        println(item)
                        item.toString() + that 
                    }
                }"""
                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }
    }
})
