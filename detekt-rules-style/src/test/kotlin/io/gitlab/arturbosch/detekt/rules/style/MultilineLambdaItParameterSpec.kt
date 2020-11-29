package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MultilineLambdaItParameterSpec : Spek({
    val subject by memoized { MultilineLambdaItParameter(Config.empty) }

    describe("MultilineLambdaItParameter rule") {
        context("single parameter, multiline lambda") {
            it("reports when parameter name is implicit `it`") {
                val findings = subject.compileAndLint(
                    """
                fun f() {
                    val digits = 1234.let { 
                        listOf(it)
                    }
                }""")
                assertThat(findings).hasSize(1)
            }
            it("reports when parameter name is explicit `it`") {
                val findings = subject.compileAndLint(
                    """
                fun f() {
                    val digits = 1234.let { it ->
                        listOf(it)
                    }
                }""")
                assertThat(findings).hasSize(1)
            }
            it("does not report when parameter name is explicit and not `it`") {
                val findings = subject.compileAndLint("""
                fun f() {
                    val digits = 1234.let { explicitParameterName ->
                        listOf(explicitParameterName)
                    }
                }""")
                assertThat(findings).hasSize(0)
            }
        }

        context("single parameter, single-line lambda") {
            it("does not report when parameter name is an implicit `it`") {
                val findings = subject.compileAndLint(
                    """
                fun f() {
                    val digits = 1234.let { listOf(it) }
                }""")
                assertThat(findings).hasSize(0)
            }
            it("does not report when parameter name is an explicit `it`") {
                val findings = subject.compileAndLint("""
                fun f() {
                    val digits = 1234.let { it -> listOf(it) }
                }""")
                assertThat(findings).hasSize(0)
            }
            it("does not report when parameter name is explicit and not `it`") {
                val findings = subject.compileAndLint("""
                fun f() {
                    val digits = 1234.let { explicit -> listOf(explicit) }
                }""")
                assertThat(findings).hasSize(0)
            }
        }

        context("multiple parameters, multiline lambda") {
            it("reports when one of the explicit parameters is an `it`") {
                val findings = subject.compileAndLint("""
                fun f() {
                    val flat = listOf(listOf(1), listOf(2)).mapIndexed { index, it -> 
                        it + index 
                    }
                }""")
                assertThat(findings).hasSize(1)
            }
            it("does not report when none of the explicit parameters is an `it`") {
                val findings = subject.compileAndLint("""
                fun f() {
                    val lambda = { item: Int, that: String -> 
                        item.toString() + that 
                    }
                }""")
                assertThat(findings).hasSize(0)
            }
        }
    }
})
