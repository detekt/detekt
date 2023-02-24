package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val MAX = "max"
private const val EXCLUDED_FUNCTIONS = "excludedFunctions"
private const val EXCLUDE_LABELED = "excludeLabeled"
private const val EXCLUDE_RETURN_FROM_LAMBDA = "excludeReturnFromLambda"
private const val EXCLUDE_GUARD_CLAUSES = "excludeGuardClauses"

class ReturnCountSpec {

    @Nested
    inner class `a function without a body` {
        val code = """
            fun func() = Unit
        """.trimIndent()

        @Test
        fun `does not report violation by default`() {
            assertThat(ReturnCount(Config.empty).compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `a function with an empty body` {
        val code = """
            fun func() {}
        """.trimIndent()

        @Test
        fun `does not report violation by default`() {
            assertThat(ReturnCount(Config.empty).compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `a file with an if condition guard clause and 2 returns` {
        val code = """
            fun test(x: Int): Int {
                if (x < 4) return 0
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                }
                return 6
            }
        """.trimIndent()

        @Test
        fun `should not get flagged for if condition guard clauses`() {
            val findings = ReturnCount(TestConfig(EXCLUDE_GUARD_CLAUSES to "true"))
                .compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a file with an if condition guard clause with body and 2 returns` {
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
        """.trimIndent()

        @Test
        fun `should not get flagged for if condition guard clauses`() {
            val findings = ReturnCount(TestConfig(EXCLUDE_GUARD_CLAUSES to "true"))
                .compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should get flagged without guard clauses`() {
            val findings = ReturnCount(TestConfig(EXCLUDE_GUARD_CLAUSES to "false"))
                .compileAndLint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `reports a too-complicated if statement for being a guard clause` {
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
        """.trimIndent()

        @Test
        fun `should report a too-complicated if statement for being a guard clause, with EXCLUDE_GUARD_CLAUSES on`() {
            val findings = ReturnCount(TestConfig(EXCLUDE_GUARD_CLAUSES to "true"))
                .compileAndLint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `a file with an ELVIS operator guard clause and 2 returns` {
        val code = """
            fun test(x: Int): Int {
                val y = x ?: return 0
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                }
                return 6
            }
        """.trimIndent()

        @Test
        fun `should not get flagged for ELVIS operator guard clauses`() {
            val findings = ReturnCount(TestConfig(EXCLUDE_GUARD_CLAUSES to "true"))
                .compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a file with 2 returns and an if condition guard clause which is not the first statement` {
        val code = """
            fun test(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                }
                if (x < 4) return 0
                return 6
            }
        """.trimIndent()

        @Test
        fun `should get flagged for an if condition guard clause which is not the first statement`() {
            val findings = ReturnCount(TestConfig(EXCLUDE_GUARD_CLAUSES to "true"))
                .compileAndLint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `a file with 2 returns and an ELVIS guard clause which is not the first statement` {
        val code = """
            fun test(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                }
                val y = x ?: return 0
                return 6
            }
        """.trimIndent()

        @Test
        fun `should get flagged for an ELVIS guard clause which is not the first statement`() {
            val findings = ReturnCount(TestConfig(EXCLUDE_GUARD_CLAUSES to "true"))
                .compileAndLint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `a file with multiple guard clauses` {
        val code = """
            var x = 1
            fun multipleGuards(a: Int?, b: Any?, c: Int?, d: Int?) {
                if(a == null) return
                val models = b as? Int ?: return
                val position = c?.takeIf { it != -1 } ?: return
                x = d ?: return
                if(b !is String) {
                    println("b is not a String")
                    return
                }
            
                return
            }
        """.trimIndent()

        @Test
        fun `should not count all four guard clauses`() {
            val findings = ReturnCount(
                TestConfig(
                    EXCLUDE_GUARD_CLAUSES to "true"
                )
            ).compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should count all four guard clauses`() {
            val findings = ReturnCount(
                TestConfig(
                    EXCLUDE_GUARD_CLAUSES to "false"
                )
            ).compileAndLint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `a file with 3 returns` {
        private val code = """
            fun test(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                    3 -> return 3
                }
                return 6
            }
        """.trimIndent()

        @Test
        fun `should get flagged by default`() {
            val findings = ReturnCount().compileAndLint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not get flagged when max value is 3`() {
            val findings = ReturnCount(TestConfig(MAX to "3")).compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should get flagged when max value is 1`() {
            val findings = ReturnCount(TestConfig(MAX to "1")).compileAndLint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `a file with 2 returns` {
        private val code = """
            fun test(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                }
                return 6
            }
        """.trimIndent()

        @Test
        fun `should not get flagged by default`() {
            val findings = ReturnCount().compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not get flagged when max value is 2`() {
            val findings = ReturnCount(TestConfig(MAX to "2")).compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should get flagged when max value is 1`() {
            val findings = ReturnCount(TestConfig(MAX to "1")).compileAndLint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `a function is ignored` {
        private val code = """
            fun test(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                    3 -> return 3
                }
                return 6
            }
        """.trimIndent()

        @Test
        fun `should not get flagged`() {
            val findings = ReturnCount(
                TestConfig(
                    MAX to "2",
                    EXCLUDED_FUNCTIONS to "test",
                )
            ).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a subset of functions are ignored` {
        private val code = """
            fun factorial(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                    3 -> return 3
                }
                return 6
            }
            
            fun fac(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                    3 -> return 3
                }
                return 6
            }
            
            fun fansOfFactorial(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> return 4
                    3 -> return 3
                }
                return 6
            }
        """.trimIndent()

        @Test
        fun `should flag none of the ignored functions`() {
            val findings = ReturnCount(
                TestConfig(
                    MAX to "2",
                    EXCLUDED_FUNCTIONS to listOf("factorial", "fac"),
                )
            ).compileAndLint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should flag none of the ignored functions using globbing`() {
            val findings = ReturnCount(
                TestConfig(
                    MAX to "2",
                    EXCLUDED_FUNCTIONS to listOf("fa*ctorial"),
                )
            ).compileAndLint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `a function with inner object` {
        private val code = """
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
        """.trimIndent()

        @Test
        fun `should not get flag when returns is in inner object`() {
            val findings = ReturnCount(TestConfig(MAX to "2")).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a function with 2 inner object` {
        private val code = """
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
        """.trimIndent()

        @Test
        fun `should not get flag when returns is in inner object`() {
            val findings = ReturnCount(TestConfig(MAX to "2")).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `a function with 2 inner object and exceeded max` {
        private val code = """
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
        """.trimIndent()

        @Test
        fun `should get flagged when returns is in inner object`() {
            val findings = ReturnCount(TestConfig(MAX to "2")).compileAndLint(code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `function with multiple labeled return statements` {
        private val code = """
            fun readUsers(name: String): Flowable<User> {
            return userDao.read(name)
                .flatMap {
                    if (it.isEmpty()) return@flatMap Flowable.empty<User>()
                    return@flatMap Flowable.just(it[0])
                }
            }
        """.trimIndent()

        @Test
        fun `should not count labeled returns from lambda by default`() {
            val findings = ReturnCount().lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `should count labeled returns from lambda when activated`() {
            val findings = ReturnCount(
                TestConfig(EXCLUDE_RETURN_FROM_LAMBDA to "false")
            ).lint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should be empty when labeled returns are de-activated`() {
            val findings = ReturnCount(
                TestConfig(
                    EXCLUDE_LABELED to "true",
                    EXCLUDE_RETURN_FROM_LAMBDA to "false",
                )
            ).lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `function with lambda which has explicit label` {
        private val code = """
            fun test() {
                listOf(1, 2, 3, 4, 5).forEach lit@{
                    if (it == 3) return@lit
                    if (it == 4) return@lit
                }
                return
            }
        """.trimIndent()

        @Test
        fun `should count labeled return of lambda with explicit label`() {
            val findings = ReturnCount(TestConfig(EXCLUDE_RETURN_FROM_LAMBDA to "false")).compileAndLint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not count labeled return of lambda with explicit label when deactivated by default`() {
            val findings = ReturnCount().compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `excludeReturnFromLambda should take precedence over excludeLabeled`() {
            val findings = ReturnCount(
                TestConfig(
                    EXCLUDE_RETURN_FROM_LAMBDA to "true",
                    EXCLUDE_LABELED to "false",
                )
            ).compileAndLint(code)
            assertThat(findings).isEmpty()
        }
    }
}
