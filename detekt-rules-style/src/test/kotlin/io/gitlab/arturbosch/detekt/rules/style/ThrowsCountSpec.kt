package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ThrowsCountSpec : Spek({

    describe("ThrowsCount rule") {

        context("a function with an empty body") {
            val code = """
                fun func() {}
            """

            it("does not report violation by default") {
                assertThat(ThrowsCount(Config.empty).lint(code)).isEmpty()
            }
        }

        context("a function without a body") {
            val code = """
                fun func() = Unit
            """

            it("does not report violation by default") {
                assertThat(ThrowsCount(Config.empty).lint(code)).isEmpty()
            }
        }

        context("code with 2 throw expressions") {
            val code = """
                fun f2(x: Int) {
                    when (x) {
                        1 -> throw IOException()
                        2 -> throw IOException()
                    }
                }
            """
            val subject by memoized { ThrowsCount(Config.empty) }

            it("does not report violation") {
                assertThat(subject.lint(code)).isEmpty()
            }
        }

        context("code with 3 throw expressions") {
            val code = """
                fun f1(x: Int) {
                    when (x) {
                        1 -> throw IOException()
                        2 -> throw IOException()
                        3 -> throw IOException()
                    }
                }
            """
            val subject by memoized { ThrowsCount(Config.empty) }

            it("reports violation by default") {
                assertThat(subject.lint(code)).hasSize(1)
            }
        }

        context("code with an override function with 3 throw expressions") {
            val code = """
                override fun f3(x: Int) { // does not report overridden function
                    when (x) {
                        1 -> throw IOException()
                        2 -> throw IOException()
                        3 -> throw IOException()
                    }
                }
            """
            val subject by memoized { ThrowsCount(Config.empty) }

            it("reports violation by default") {
                assertThat(subject.lint(code)).isEmpty()
            }
        }

        context("max count == 3") {
            val code = """
                fun f4(x: String?) {
                    val denulled = x ?: throw IOException()
                    val int = x?.toInt() ?: throw IOException()
                    val double = x?.toDouble() ?: throw IOException()
                }
            """

            it("does not report when max parameter is 3") {
                val config = TestConfig(mapOf(ThrowsCount.MAX to "3"))
                val subject = ThrowsCount(config)
                assertThat(subject.lint(code)).isEmpty()
            }

            it("reports violation when max parameter is 2") {
                val config = TestConfig(mapOf(ThrowsCount.MAX to "2"))
                val subject = ThrowsCount(config)
                assertThat(subject.lint(code)).hasSize(1)
            }
        }

        context("code with ELVIS operator guard clause") {
            val codeWithGuardClause = """
                fun test(x: Int): Int {
                    val y = x ?: throw Exception()
                    when (x) {
                        5 -> println("x=5")
                        4 -> throw Exception()
                    }
                    throw Exception()
                }
            """

            it("should not report violation with EXCLUDE_GUARD_CLAUSES as true") {
                val config = TestConfig(mapOf(ThrowsCount.EXCLUDE_GUARD_CLAUSES to "true"))
                val subject = ThrowsCount(config)
                assertThat(subject.lint(codeWithGuardClause)).isEmpty()
            }

            it("should report violation with EXCLUDE_GUARD_CLAUSES as false") {
                val config = TestConfig(mapOf(ThrowsCount.EXCLUDE_GUARD_CLAUSES to "false"))
                val subject = ThrowsCount(config)
                assertThat(subject.lint(codeWithGuardClause)).hasSize(1)
            }
        }

        context("code with if condition guard clause") {
            val codeWithGuardClause = """
                fun test(x: Int): Int {
                    if(x == null) throw Exception()
                    when (x) {
                        5 -> println("x=5")
                        4 -> throw Exception()
                    }
                    throw Exception()
                }
            """

            it("should not report violation with EXCLUDE_GUARD_CLAUSES as true") {
                val config = TestConfig(mapOf(ThrowsCount.EXCLUDE_GUARD_CLAUSES to "true"))
                val subject = ThrowsCount(config)
                assertThat(subject.lint(codeWithGuardClause)).isEmpty()
            }

            it("should report violation with EXCLUDE_GUARD_CLAUSES as false") {
                val config = TestConfig(mapOf(ThrowsCount.EXCLUDE_GUARD_CLAUSES to "false"))
                val subject = ThrowsCount(config)
                assertThat(subject.lint(codeWithGuardClause)).hasSize(1)
            }
        }

        context("reports a too-complicated if statement for being a guard clause") {
            val codeWithIfCondition = """
                fun test(x: Int): Int {
                    if (x < 4) {
                        println("x x is less than 4")
                        if (x < 2) {
                          println("x is also less than 2")
                          throw Exception()
                        }
                        throw Exception()
                    }
                    when (x) {
                        5 -> println("x=5")
                        4 -> throw Exception()
                    }
                    throw Exception()
                }
            """

            it("should report violation even with EXCLUDE_GUARD_CLAUSES as true") {
                val config = TestConfig(mapOf(ThrowsCount.EXCLUDE_GUARD_CLAUSES to "true"))
                val subject = ThrowsCount(config)
                assertThat(subject.lint(codeWithIfCondition)).hasSize(1)
            }
        }

        context("a file with 2 returns and an if condition guard clause which is not the first statement") {
            val codeWithIfCondition = """
                fun test(x: Int): Int {
                    when (x) {
                        5 -> println("x=5")
                        4 -> throw Exception()
                    }
                    if (x < 4) throw Exception()
                    throw Exception()
                }
            """

            it("should report the violation even with EXCLUDE_GUARD_CLAUSES as true") {
                val config = TestConfig(mapOf(ThrowsCount.EXCLUDE_GUARD_CLAUSES to "true"))
                val subject = ThrowsCount(config)
                assertThat(subject.lint(codeWithIfCondition)).hasSize(1)
            }
        }
        context("a file with 2 returns and an ELVIS guard clause which is not the first statement") {
            val codeWithIfCondition = """
                fun test(x: Int): Int {
                    when (x) {
                        5 -> println("x=5")
                        4 -> throw Exception()
                    }
                    val y = x ?: throw Exception()
                    throw Exception()
                }
            """

            it("should report the violation even with EXCLUDE_GUARD_CLAUSES as true") {
                val config = TestConfig(mapOf(ThrowsCount.EXCLUDE_GUARD_CLAUSES to "true"))
                val subject = ThrowsCount(config)
                assertThat(subject.lint(codeWithIfCondition)).hasSize(1)
            }
        }

        context("a file with multiple guard clauses") {
            val codeWithMultipleGuardClauses = """
                fun multipleGuards(a: Int?, b: Any?, c: Int?) {
                    if(a == null) throw Exception()
                    val models = b as? Int ?: throw Exception()
                    val position = c?.takeIf { it != -1 } ?: throw Exception()
                    if(b !is String) {
                        println("b is not a String")
                        throw Exception()
                    }

                    throw Exception()
                }
            """

            it("should not report violation with EXCLUDE_GUARD_CLAUSES as true") {
                val config = TestConfig(mapOf(ThrowsCount.EXCLUDE_GUARD_CLAUSES to "true"))
                val subject = ThrowsCount(config)
                assertThat(subject.lint(codeWithMultipleGuardClauses)).isEmpty()
            }

            it("should report violation with EXCLUDE_GUARD_CLAUSES as false") {
                val config = TestConfig(mapOf(ThrowsCount.EXCLUDE_GUARD_CLAUSES to "false"))
                val subject = ThrowsCount(config)
                assertThat(subject.lint(codeWithMultipleGuardClauses)).hasSize(1)
            }
        }
    }
})
