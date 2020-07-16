package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ReturnCountSpec : Spek({

    describe("ReturnCount rule") {

        context("a function without a body") {
            val code = """
                fun func() = Unit
            """

            it("does not report violation by default") {
                assertThat(ReturnCount(Config.empty).compileAndLint(code)).isEmpty()
            }
        }

        context("a function with an empty body") {
            val code = """
                fun func() {}
            """

            it("does not report violation by default") {
                assertThat(ReturnCount(Config.empty).compileAndLint(code)).isEmpty()
            }
        }

        context("a file with an if condition guard clause and 2 returns") {
            val code = """
            fun test(x: Int): Int {
                if (x < 4) return 0
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                }
                return 6
            }
        """

            it("should not get flagged for if condition guard clauses") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.EXCLUDE_GUARD_CLAUSES to "true")))
                    .compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a file with an if condition guard clause with body and 2 returns") {
            val code = """
            fun test(x: Int): Int {
                if (x < 4) {
                    println("x x is less than 4")
                    return 0
                }
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                }
                return 6
            }
        """

            it("should not get flagged for if condition guard clauses") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.EXCLUDE_GUARD_CLAUSES to "true")))
                    .compileAndLint(code)
                assertThat(findings).isEmpty()
            }

            it("should get flagged without guard clauses") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.EXCLUDE_GUARD_CLAUSES to "false")))
                    .compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("reports a too-complicated if statement for being a guard clause") {
            val code = """
            fun test(x: Int): Int {
                if (x < 4) {
                    println("x x is less than 4")
                    if (x < 2) {
                      println("x is also less than 2")
                      return 1
                    }
                    return 0
                }
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                }
                return 6
            }
        """

            it("should report a too-complicated if statement for being a guard clause, with EXCLUDE_GUARD_CLAUSES on") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.EXCLUDE_GUARD_CLAUSES to "true")))
                    .compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a file with an ELVIS operator guard clause and 2 returns") {
            val code = """
            fun test(x: Int): Int {
                val y = x ?: return 0
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                }
                return 6
            }
        """

            it("should not get flagged for ELVIS operator guard clauses") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.EXCLUDE_GUARD_CLAUSES to "true")))
                    .compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a file with 2 returns and an if condition guard clause which is not the first statement") {
            val code = """
            fun test(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                }
                if (x < 4) return 0
                return 6
            }
        """

            it("should get flagged for an if condition guard clause which is not the first statement") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.EXCLUDE_GUARD_CLAUSES to "true")))
                    .compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a file with 2 returns and an ELVIS guard clause which is not the first statement") {
            val code = """
            fun test(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                }
                val y = x ?: return 0
                return 6
            }
        """

            it("should get flagged for an ELVIS guard clause which is not the first statement") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.EXCLUDE_GUARD_CLAUSES to "true")))
                    .compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a file with multiple guard clauses") {
            val code = """
                fun multipleGuards(a: Int?, b: Any?, c: Int?) {
                    if(a == null) return
                    val models = b as? Int ?: return
                    val position = c?.takeIf { it != -1 } ?: return
                    if(b !is String) {
                        println("b is not a String")
                        return
                    }

                    return
                }
            """.trimIndent()

            it("should not count all four guard clauses") {
                val findings = ReturnCount(TestConfig(
                    ReturnCount.EXCLUDE_GUARD_CLAUSES to "true"
                )).compileAndLint(code)
                assertThat(findings).isEmpty()
            }

            it("should count all four guard clauses") {
                val findings = ReturnCount(TestConfig(
                    ReturnCount.EXCLUDE_GUARD_CLAUSES to "false"
                )).compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a file with 3 returns") {
            val code = """
            fun test(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                    3 -> return 3
                }
                return 6
            }
        """

            it("should get flagged by default") {
                val findings = ReturnCount().compileAndLint(code)
                assertThat(findings).hasSize(1)
            }

            it("should not get flagged when max value is 3") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.MAX to "3"))).compileAndLint(code)
                assertThat(findings).isEmpty()
            }

            it("should get flagged when max value is 1") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.MAX to "1"))).compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a file with 2 returns") {
            val code = """
            fun test(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                }
                return 6
            }
        """

            it("should not get flagged by default") {
                val findings = ReturnCount().compileAndLint(code)
                assertThat(findings).isEmpty()
            }

            it("should not get flagged when max value is 2") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.MAX to "2"))).compileAndLint(code)
                assertThat(findings).isEmpty()
            }

            it("should get flagged when max value is 1") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.MAX to "1"))).compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a function is ignored") {
            val code = """
            fun test(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                    3 -> return 3
                }
                return 6
            }
        """

            it("should not get flagged") {
                val findings = ReturnCount(TestConfig(mapOf(
                    ReturnCount.MAX to "2",
                    ReturnCount.EXCLUDED_FUNCTIONS to "test")
                )).compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a subset of functions are ignored") {
            val code = """
            fun test1(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                    3 -> return 3
                }
                return 6
            }

            fun test2(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                    3 -> return 3
                }
                return 6
            }

            fun test3(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                    3 -> return 3
                }
                return 6
            }
        """

            it("should flag none of the ignored functions") {
                val findings = ReturnCount(TestConfig(mapOf(
                    ReturnCount.MAX to "2",
                    ReturnCount.EXCLUDED_FUNCTIONS to "test1,test2")
                )).compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("a function with inner object") {
            val code = """
            fun test(x: Int): Int {
                val a = object {
                    fun test2(x: Int): Int {
                        when (x) {
                            5 -> println("x=5")
                            else -> return 0
                        }
                        return 6
                    }
                }
                when (x) {
                    5 -> println("x=5")
                    else -> return 0
                }
                return 6
            }
        """

            it("should not get flag when returns is in inner object") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.MAX to "2"))).compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a function with 2 inner object") {
            val code = """
            fun test(x: Int): Int {
                val a = object {
                    fun test2(x: Int): Int {
                        val b = object {
                            fun test3(x: Int): Int {
                                when (x) {
                                    5 -> println("x=5")
                                    else -> return 0
                                }
                                return 6
                            }
                        }
                        when (x) {
                            5 -> println("x=5")
                            else -> return 0
                        }
                        return 6
                    }
                }
                when (x) {
                    5 -> println("x=5")
                    else -> return 0
                }
                return 6
            }
        """

            it("should not get flag when returns is in inner object") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.MAX to "2"))).compileAndLint(code)
                assertThat(findings).isEmpty()
            }
        }

        context("a function with 2 inner object and exceeded max") {
            val code = """
            fun test(x: Int): Int {
                val a = object {
                    fun test2(x: Int): Int {
                        val b = object {
                            fun test3(x: Int): Int {
                                when (x) {
                                    5 -> println("x=5")
                                    else -> return 0
                                }
                                return 6
                            }
                        }
                        when (x) {
                            5 -> println("x=5")
                            else -> return 0
                        }
                        return 6
                    }
                }
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                    3 -> return 3
                    else -> return 0
                }
                return 6
            }
        """

            it("should get flagged when returns is in inner object") {
                val findings = ReturnCount(TestConfig(mapOf(ReturnCount.MAX to "2"))).compileAndLint(code)
                assertThat(findings).hasSize(1)
            }
        }

        context("function with multiple labeled return statements") {

            val code = """
            fun readUsers(name: String): Flowable<User> {
            return userDao.read(name)
                .flatMap {
                    if (it.isEmpty()) return@flatMap Flowable.empty<User>()
                    return@flatMap Flowable.just(it[0])
                }
            }
        """

            it("should not count labeled returns from lambda by default") {
                val findings = ReturnCount().lint(code)
                assertThat(findings).isEmpty()
            }

            it("should count labeled returns from lambda when activated") {
                val findings = ReturnCount(
                    TestConfig(mapOf(ReturnCount.EXCLUDE_RETURN_FROM_LAMBDA to "false"))).lint(code)
                assertThat(findings).hasSize(1)
            }

            it("should be empty when labeled returns are de-activated") {
                val findings = ReturnCount(
                    TestConfig(mapOf(
                        ReturnCount.EXCLUDE_LABELED to "true",
                        ReturnCount.EXCLUDE_RETURN_FROM_LAMBDA to "false"
                    ))).lint(code)
                assertThat(findings).isEmpty()
            }
        }
    }
})
