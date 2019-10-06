package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class OptionalUnitSpec : Spek({
    val subject by memoized { OptionalUnit(Config.empty) }

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
            it("should not report Unit as part of default interface implementations") {
                val code = """
                    interface Foo {
                        fun method(i: Int) = Unit
                    }
                """
                val findings = subject.compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }
    }
})
