package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NamedArgumentsSpec : Spek({

    val defaultThreshold = 2
    val defaultConfig by memoized {
        TestConfig(
            mapOf(
                NamedArguments.THRESHOLD to defaultThreshold,
            )
        )
    }

    val subject by memoized { NamedArguments(defaultConfig) }

    describe("NameArguments rule") {

        val errorMessage = "Function invocation with more number of parameters must be named."
        it("invocation with more than 2 parameters should throw error") {
            val code = """
                fun sum(a: Int, b:Int, c:Int) {
                    println(a + b + c)
                }
                fun call() {
                    sum(1, 2, 3)
                }
                """
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo(errorMessage)
        }

        it("Function invocation with more than 2 parameters should not throw error if named") {
            val code = """
                fun sum(a: Int, b:Int, c:Int) {
                    println(a + b + c)
                }
                fun call() {
                    sum(a = 1, b = 2, c = 3)
                }
                """
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(0)
        }

        it("invocation with more than 2 parameters should throw error if even one is not named") {
            val code = """
                fun sum(a: Int, b:Int, c:Int) {
                    println(a + b + c)
                }
                fun call() {
                    sum(1, b = 2, c = 3)
                }
                """
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo(errorMessage)
        }

        it("invocation with less than 3 parameters should not throw error") {
            val code = """
                fun sum(a: Int, b:Int) {
                    println(a + b)
                }
                fun call() {
                    sum(1, 2)
                }
                """
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(0)
        }

        it("invocation with less than 3 named parameters should not throw error") {
            val code = """
                fun sum(a: Int, b:Int) {
                    println(a + b)
                }
                fun call() {
                    sum(a = 1, b = 2)
                }
                """
            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(0)
        }
    }
})
