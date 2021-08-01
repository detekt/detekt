package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NoNameShadowingSpec : Spek({
    setupKotlinEnvironment()
    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { NoNameShadowing() }

    describe("NoNameShadowing rule") {
        it("report shadowing variable") {
            val code = """
                fun test(i: Int) {
                    val i = 1
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(2, 9)
            assertThat(findings[0]).hasMessage("Name shadowed: i")
        }

        it("report shadowing destructuring declaration entry") {
            val code = """
                fun test(j: Int) {
                    val (j, _) = 1 to 2
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage("Name shadowed: j")
        }

        it("report shadowing lambda parameter") {
            val code = """
                fun test(k: Int) {
                    listOf(1).map { k ->
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage("Name shadowed: k")
        }

        it("report shadowing nested lambda 'it' parameter") {
            val code = """
                fun test() {
                    listOf(1).forEach {
                        listOf(2).forEach { it ->
                        }
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage("Name shadowed: it")
        }

        it("does not report when implicit 'it' parameter isn't used") {
            val code = """
                fun test() {
                    listOf(1).forEach {
                        listOf(2).forEach {
                        }
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("does not report not shadowing variable") {
            val code = """
                fun test(i: Int) {
                    val j = i
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("does not report not shadowing nested lambda implicit 'it' parameter") {
            val code = """
                fun test() {
                    listOf(1).forEach { i ->
                        listOf(2).forEach {
                            println(it)
                        }
                    }
                    "".run {
                        listOf(2).forEach {
                            println(it)
                        }
                    }
                    listOf("").let { list ->
                        list.map { it + "x" }
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
})
