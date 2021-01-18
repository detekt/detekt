package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ReturnFromFinallySpec : Spek({
    setupKotlinEnvironment()

    val subject by memoized { ReturnFromFinally() }
    val env: KotlinCoreEnvironment by memoized()

    describe("ReturnFromFinally rule") {

        context("a finally block with a return statement") {
            val code = """
                fun x() {
                    try {
                    } finally {
                        return
                    }
                }
            """

            it("should report") {
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a finally block with no return statement") {
            val code = """
                fun x() {
                    try {
                    } finally {
                    }
                }
            """

            it("should not report") {
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        context("a finally block with a nested return statement") {
            val code = """
                fun x() {
                    try {
                    } finally {
                        if (1 == 1) {
                            return
                        }
                    }
                }
            """

            it("should report") {
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a finally block with a return in an inner function") {
            val code = """
                fun x() {
                    try {
                    } finally {
                        fun y() {
                            return
                        }
                        y()
                    }
                }
            """

            it("should not report") {
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        context("a finally block with a return as labelled expression") {
            val code = """
                fun x() {
                    label@{ 
                        try {
                        } finally {
                            return@label
                        }
                    }
                }
            """
            it("should report when ignoreLabeled is false") {
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            it("should not report when ignoreLabeled is true") {
                val config = TestConfig(mapOf(ReturnFromFinally.IGNORE_LABELED to "true"))
                val findings = ReturnFromFinally(config).compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }

        context("a finally block as expression for property") {
            it("should report") {
                val code = """
                    val expression = try {
                        "try"
                    } catch (e: Exception) {
                        "exception"
                    } finally {
                        "finally"
                    }
                """

                val finding = subject.compileAndLintWithContext(env, code)

                assertThat(finding).hasSize(1)
            }
        }

        context("a finally block as expression for method") {
            it("should report") {
                val code = """
                    fun expression() = try {
                        "try"
                    } catch (e: Exception) {
                        "exception"
                    } finally {
                        "finally"
                    }
                """

                val finding = subject.compileAndLintWithContext(env, code)

                assertThat(finding).hasSize(1)
            }
        }

        context("when a finally block called method that return value") {
            it("should report") {
                val code = """
                    fun expression() = try {
                        "try"
                    } catch (e: Exception) {
                        "exception"
                    } finally {
                        compute()
                    }
                    
                    fun compute(): String = "value"
                """

                val finding = subject.compileAndLintWithContext(env, code)

                assertThat(finding).hasSize(1)
            }
        }

        context("when finally block absents in expression for property") {
            it("shouldn't report") {
                val code = """
                    val expression = try {
                        "try"
                    } catch (e: Exception) {
                        "exception"
                    } 
                """

                val finding = subject.compileAndLintWithContext(env, code)

                assertThat(finding).hasSize(0)
            }
        }

        context("when finally block absents in expression for method") {

            it("shouldn't report") {
                val code = """
                    fun expression() = try {
                        "try"
                    } catch (e: Exception) {
                        "exception"
                    }
                """

                val finding = subject.compileAndLintWithContext(env, code)

                assertThat(finding).hasSize(0)
            }
        }

        context("when try catch finally block is independent") {
            it("shouldn't report") {
                val code = """
                   fun expression() {
                       try {
                           "try"
                       } catch (e: Exception) {
                           "exception"
                       } finally {
                           "finally"
                       }
                   }
                """

                val finding = subject.compileAndLintWithContext(env, code)

                assertThat(finding).hasSize(0)
            }
        }

        context("when finally block doesn't contain return value") {
            it("shouldn't report") {
                val code = """
                    val expression = try {
                        "try"
                    } catch (e: Exception) {
                        "exception"
                    } finally {
                        println("finally")
                    }
                """

                val finding = subject.compileAndLintWithContext(env, code)

                assertThat(finding).hasSize(0)
            }
        }

        context("when return value in finally block is property") {
            it("should report") {
                val code = """
                    val property: String = "property"
                    val expression = try {
                        "try"
                    } catch (e: Exception) {
                        "exception"
                    } finally {
                        println("finally")
                        property
                    }
                """

                val finding = subject.compileAndLintWithContext(env, code)

                assertThat(finding).hasSize(1)
            }
        }
    }
})
