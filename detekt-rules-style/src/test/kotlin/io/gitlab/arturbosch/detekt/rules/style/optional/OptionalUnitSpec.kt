package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class OptionalUnitSpec : Spek({

    setupKotlinEnvironment()

    val subject by memoized { OptionalUnit(Config.empty) }
    val env: KotlinCoreEnvironment by memoized()

    describe("OptionalUnit rule") {

        context("several functions which return Unit") {

            val code = """
                fun returnsUnit1(): Unit {
                    fun returnsUnitNested(): Unit {
                        return Unit
                    }
                    return Unit
                }

                fun returnsUnit2() = Unit
            """
            lateinit var findings: List<Finding>

            beforeEachTest {
                findings = subject.compileAndLint(code)
            }

            it("should report functions returning Unit") {
                assertThat(findings).hasSize(3)
            }

            it("should report the correct violation message") {
                findings.forEach {
                    assertThat(it.message).endsWith(
                        " defines a return type of Unit. This is unnecessary and can safely be removed.")
                }
            }
        }

        context("an overridden function which returns Unit") {

            it("should not report Unit return type in overridden function") {
                val code = """
                    interface I {
                        fun returnsUnit()
                    }
                    class C : I {
                        override fun returnsUnit() = Unit
                    }
                """
                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("several lone Unit statements") {

            val code = """
                fun returnsNothing() {
                    Unit
                    val i: (Int) -> Unit = { _ -> Unit }
                    if (true) {
                        Unit
                    }
                }

                class A {
                    init {
                        Unit
                    }
                }
            """
            lateinit var findings: List<Finding>

            beforeEachTest {
                findings = subject.compileAndLint(code)
            }

            it("should report lone Unit statement") {
                assertThat(findings).hasSize(4)
            }

            it("should report the correct violation message") {
                findings.forEach {
                    assertThat(it.message).isEqualTo("A single Unit expression is unnecessary and can safely be removed")
                }
            }
        }

        context("several Unit references") {

            it("should not report Unit reference") {
                val findings = subject.compileAndLint("""
                    fun returnsNothing(u: Unit, us: () -> String) {
                        val u1 = u is Unit
                        val u2: Unit = Unit
                        val Unit = 1
                        Unit.equals(null)
                        val i: (Int) -> Unit = { _ -> }
                    }
                """)
                assertThat(findings).isEmpty()
            }
        }

        context("a default interface implementation") {
            it("should report Unit as part of default interface implementations") {
                val code = """
                    interface Foo {
                        fun method(i: Int) = Unit
                    }
                """
                val findings = subject.compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("last statement in block - #2452") {
            it("unused as an expression") {
                val code = """
                    fun test(i: Int, b: Boolean) {
                        when (i) {
                            0 -> println(1)
                            else -> {
                                if (b) {
                                    println(2)
                                }
                                Unit
                            }
                        }
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            it("used as an expression and the previous expression is not a Unit type") {
                val code = """
                    fun <T> T.foo() {
                        println(this)
                    }
                    
                    fun test(i: Int, b: Boolean) {
                        when (i) {
                            0 -> println(1)
                            else -> {
                                1
                                Unit
                            }
                        }.foo()
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            it("used as an expression and the previous expression cannot be used as a value") {
                val code = """
                    fun <T> T.foo() {
                        println(this)
                    }
                    
                    fun test(i: Int, j: Int) {
                        when (i) {
                            0 -> println(1)
                            else -> {
                                if (j == 1) {
                                    println(2)
                                } else if (j == 2) {
                                    println(3)
                                } else if (j == 3) {
                                    println(4)
                                }
                                Unit
                            }
                        }.foo()
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            it("used as an expression and the previous expression cannot be used as a value 2") {
                val code = """
                    fun <T> T.foo() {
                        println(this)
                    }
                    
                    fun test(i: Int, j: Int) {
                        when (i) {
                            0 -> println(1)
                            else -> {
                                when (j) {
                                    1 -> println(2)
                                    2 -> println(3)
                                    3 -> println(4)
                                }
                                Unit
                            }
                        }.foo()
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            it("used as an expression and the previous expression can be used as a value") {
                val code = """
                    fun <T> T.foo() {
                        println(this)
                    }
                    
                    fun test(i: Int, j: Int) {
                        when (i) {
                            0 -> println(1)
                            else -> {
                                if (j == 1) {
                                    println(2)
                                } else if (j == 2) {
                                    println(3)
                                } else if (j == 3) {
                                    println(4)
                                } else {
                                    println(5)
                                }
                                Unit
                            }
                        }.foo()
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            it("used as an expression and the previous expression can be used as a value 2") {
                val code = """
                    fun <T> T.foo() {
                        println(this)
                    }
                    
                    fun test(i: Int, j: Int) {
                        when (i) {
                            0 -> println(1)
                            else -> {
                                when (j) {
                                    1 -> println(2)
                                    2 -> println(3)
                                    3 -> println(4)
                                    else -> println(5)
                                }
                                Unit
                            }
                        }.foo()
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            it("used as an expression and the previous expression can be used as a value 3") {
                val code = """
                    fun <T> T.foo() {
                        println(this)
                    }
                    
                    enum class E { A, B } 
                    
                    fun test(i: Int, e: E) {
                        when (i) {
                            0 -> println(1)
                            else -> {
                                when (e) {
                                    E.A -> println(1)
                                    E.B -> println(2)
                                }
                                Unit
                            }
                        }.foo()
                    }
                """
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }
    }
})
