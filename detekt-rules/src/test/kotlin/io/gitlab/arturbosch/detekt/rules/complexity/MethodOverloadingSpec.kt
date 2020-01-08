package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MethodOverloadingSpec : Spek({

    val subject by memoized { MethodOverloading(threshold = 3) }

    describe("MethodOverloading rule") {

        context("several overloaded methods") {

            lateinit var findings: List<Finding>

            beforeEachTest {
                findings = subject.lint(Case.OverloadedMethods.path())
            }

            it("reports overloaded methods which exceed the threshold") {
                assertThat(findings.size).isEqualTo(3)
            }

            it("reports the correct method name") {
                val expected = "The method 'overloadedMethod' is overloaded 3 times."
                assertThat(findings[0].message).isEqualTo(expected)
            }

            it("does not report overloaded methods which do not exceed the threshold") {
                subject.compileAndLint("""
                class Test {
                    fun x() { }
                    fun x(i: Int) { }
                }""")
                assertThat(subject.findings.size).isZero()
            }
        }

        context("several overloaded extensions methods") {

            it("does not report extension methods with a different receiver") {
                subject.compileAndLint("""
                fun Boolean.foo() {}
                fun Int.foo() {}
                fun Long.foo() {}""")
                assertThat(subject.findings.size).isZero()
            }

            it("reports extension methods with the same receiver") {
                subject.compileAndLint("""
                fun Int.foo() {}
                fun Int.foo(i: Int) {}
                fun Int.foo(i: String) {}""")
                assertThat(subject.findings.size).isEqualTo(1)
            }
        }

        context("several overloaded methods inside enum classes") {

            it("does not report overridden methods inside enum entries") {
                val code = """
                    enum class Test {
                        E1 {
                            override fun f() {}
                        },
                        E2 {
                            override fun f() {}
                        },
                        E3 {
                            override fun f() {}
                        };

                        abstract fun f()
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("reports overloaded methods in enum entry") {
                val code = """
                    enum class Test {
                        E {
                            fun f(i: Int) {}
                            fun f(i: Int, j: Int) {}
                        };
                        fun f() {}
                    }
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("reports overloaded methods in enum class") {
                val code = """
                    enum class Test {
                        E;
                    
                        fun f() {}
                        fun f(i: Int) {}
                        fun f(i: Int, j: Int) {}
                    }
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }
        }
    }
})
