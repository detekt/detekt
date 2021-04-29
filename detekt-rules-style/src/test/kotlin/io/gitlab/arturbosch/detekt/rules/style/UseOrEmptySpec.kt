package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UseOrEmptySpec : Spek({
    setupKotlinEnvironment()
    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UseOrEmpty() }

    describe("report UseOrEmptySpec rule") {
        it("emptyList") {
            val code = """
                fun test(x: List<Int>?) {
                    val a = x ?: emptyList()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(2, 13)
            assertThat(findings[0]).hasMessage("This '?: emptyList()' can be replaced with 'orEmpty()' call")
        }

        it("emptySet") {
            val code = """
                fun test(x: Set<Int>?) {
                    val a = x ?: emptySet()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("emptyMap") {
            val code = """
                fun test(x: Map<Int, String>?) {
                    val a = x ?: emptyMap()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("emptySequence") {
            val code = """
                fun test(x: Sequence<Int>?) {
                    val a = x ?: emptySequence()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("emptyArray") {
            val code = """
                fun test(x: Array<Int>?) {
                    val a = x ?: emptyArray()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("listOf") {
            val code = """
                fun test(x: List<Int>?) {
                    val a = x ?: listOf()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("setOf") {
            val code = """
                fun test(x: Set<Int>?) {
                    val a = x ?: setOf()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("mapOf") {
            val code = """
                fun test(x: Map<Int, String>?) {
                    val a = x ?: mapOf()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("sequenceOf") {
            val code = """
                fun test(x: Sequence<Int>?) {
                    val a = x ?: sequenceOf()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("arrayOf") {
            val code = """
                fun test(x: Array<Int>?) {
                    val a = x ?: arrayOf()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("empty string") {
            val code = """
                fun test(x: String?) {
                    val a = x ?: ""
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        it("mutable list") {
            val code = """
                fun test(x: MutableList<Int>?) {
                    val a = x ?: emptyList()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }

    describe("does not report UseOrEmptySpec rule") {
        it("not null") {
            val code = """
                fun test(x: List<Int>) {
                    val a = x ?: emptyList()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("not empty") {
            val code = """
                fun test(x: List<Int>?) {
                    val a = x ?: listOf(1)
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("different types") {
            val code = """
                fun test(x: List<Int>?) {
                    val a = x ?: emptySet()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("mutableListOf") {
            val code = """
                fun test(x: MutableList<Int>?) {
                    val a = x ?: mutableListOf()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("intArrayOf") {
            val code = """
                fun test(x: IntArray?) {
                    val a = x ?: intArrayOf()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
})
