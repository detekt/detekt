package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ExplicitItLambdaParameterSpec : Spek({
    val subject by memoized { ExplicitItLambdaParameter(Config.empty) }

    describe("ExplicitItLambdaParameter rule") {
        context("single parameter lambda with name `it` declared explicitly") {
            it("reports when parameter type is not declared") {
                val findings = subject.compileAndLint("""
                fun f() {
                    val digits = 1234.let { it -> listOf(it) }
                }""")
                assertThat(findings).hasSize(1)
            }
            it("reports when parameter type is declared explicitly") {
                val findings = subject.compileAndLint("""
                fun f() {
                    val lambda = { it: Int -> it.toString() }
                }""")
                assertThat(findings).hasSize(1)
            }
        }
        context("no parameter declared explicitly") {
            it("does not report implicit `it` parameter usage") {
                val findings = subject.compileAndLint("""
                fun f() {
                    val lambda = { i: Int -> i.toString() }
                    val digits = 1234.let { lambda(it) }.toList()
                    val flat = listOf(listOf(1), listOf(2)).flatMap { it }
                }""")
                assertThat(findings).isEmpty()
            }
        }

        context("multiple parameters one of which with name `it` declared explicitly") {
            it("reports when parameter types are not declared") {
                val findings = subject.compileAndLint("""
                fun f() {
                    val flat = listOf(listOf(1), listOf(2)).mapIndexed { index, it -> it + index }
                }""")
                assertThat(findings).hasSize(1)
            }
            it("reports when parameter types are declared explicitly") {
                val findings = subject.compileAndLint("""
                fun f() {
                    val lambda = { it: Int, that: String -> it.toString() + that }
                }""")
                assertThat(findings).hasSize(1)
            }
        }
    }
})
