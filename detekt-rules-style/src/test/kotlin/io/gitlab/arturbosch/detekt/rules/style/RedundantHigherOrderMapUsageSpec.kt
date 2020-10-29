package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class RedundantHigherOrderMapUsageSpec : Spek({
    setupKotlinEnvironment()
    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { RedundantHigherOrderMapUsage() }

    describe("report RedundantHigherOrderMapUsage rule") {
        it("simple") {
            val code = """
                fun test() {
                    listOf(1, 2, 3)
                        .filter { it > 1 }
                        .map { it }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(4, 10)
            assertThat(findings[0]).hasMessage("This 'map' call can be removed.")
        }

        it("lambda body is not single statement") {
            val code = """
                fun doSomething() {}

                fun test() {
                    listOf(1, 2, 3)
                        .map {
                            doSomething()
                            it 
                        }
                        .filter { it > 1 }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(5, 10)
            assertThat(findings[0]).hasMessage("This 'map' call can be replaced with 'onEach' or 'forEach'.")
        }

        it("explicit lambda parameter") {
            val code = """
                fun test() {
                    listOf(1, 2, 3).map { foo -> foo }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("lambda in argument list") {
            val code = """
                fun test() {
                    listOf(1).map({ it })
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("labeled return") {
            val code = """
                fun test(list: List<Int>) {
                    list.map {
                        if (it == 1) return@map it
                        if (it == 2) return@map it
                        it
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("return for outer function") {
            val code = """
                fun doSomething() {}
                
                fun test(list: List<Int>): List<Int> {
                    return list.map {
                        if (it == 1) return emptyList()
                        doSomething()
                        it
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("return for outer lambda") {
            val code = """
                fun test(list: List<Int>): List<String> {
                    return listOf("a", "b", "c").map outer@{ s ->
                        list.map {
                            if (it == 1) return@outer "-"
                            it
                        }.joinToString("") + s
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("implicit receiver") {
            val code = """
                fun List<Int>.test() {
                    map { it }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("this receiver") {
            val code = """
                fun List<Int>.test() {
                    this.map { it }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("mutable list receiver") {
            val code = """
                fun test() {
                    mutableListOf(1).map { it }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("sequence receiver") {
            val code = """
                fun test() {
                    val x:Sequence<Int> = sequenceOf(1).map { it }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("set receiver") {
            val code = """
                fun test() {
                    setOf(1).map { it }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage("This 'map' call can be replaced with 'toList'.")
        }
    }

    describe("does not report RedundantHigherOrderMapUsage rule") {
        it("last statement is not lambda parameter") {
            val code = """
                fun test() {
                    listOf(1, 2, 3)
                        .filter { it > 1 }
                        .map { it + 1 }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("labeled return is not lambda parameter") {
            val code = """
                fun test(list: List<Int>) {
                    list.map {
                        if (it == 1) return@map 0
                        it
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("destructuring lambda parameter") {
            val code = """
                fun test() {
                    listOf(1 to 2).map { (a, b) -> a }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("map receiver") {
            val code = """
                fun test() {
                    val x: List<Map.Entry<Int, String>> = mapOf(1 to "a").map { it }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
})
