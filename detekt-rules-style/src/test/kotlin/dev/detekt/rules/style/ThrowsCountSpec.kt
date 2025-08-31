package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val MAX = "max"
private const val EXCLUDE_GUARD_CLAUSES = "excludeGuardClauses"

class ThrowsCountSpec {

    @Nested
    inner class `a function with an empty body` {
        val code = """
            fun func() {}
        """.trimIndent()

        @Test
        fun `does not report violation by default`() {
            assertThat(ThrowsCount(Config.empty).lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `a function without a body` {
        val code = """
            fun func() = Unit
        """.trimIndent()

        @Test
        fun `does not report violation by default`() {
            assertThat(ThrowsCount(Config.empty).lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `code with 2 throw expressions` {
        val code = """
            import java.io.IOException

            fun f2(x: Int) {
                when (x) {
                    1 -> throw IOException()
                    2 -> throw IOException()
                }
            }
        """.trimIndent()
        val subject = ThrowsCount(Config.empty)

        @Test
        fun `does not report violation`() {
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `code with 3 throw expressions` {
        val code = """
            import java.io.IOException

            fun f1(x: Int) {
                when (x) {
                    1 -> throw IOException()
                    2 -> throw IOException()
                    3 -> throw IOException()
                }
            }
        """.trimIndent()
        val subject = ThrowsCount(Config.empty)

        @Test
        fun `reports violation by default`() {
            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `code with an override function with 3 throw expressions` {
        val code = """
            import java.io.IOException

            override fun f3(x: Int) {
                when (x) {
                    1 -> throw IOException()
                    2 -> throw IOException()
                    3 -> throw IOException()
                }
            }
        """.trimIndent()
        val subject = ThrowsCount(Config.empty)

        @Test
        fun `reports violation by default`() {
            assertThat(subject.lint(code, compile = false)).hasSize(1)
        }
    }

    @Nested
    inner class `code with a nested function with 3 throw expressions` {
        val code = """
            import java.io.IOException
            
            fun foo(x: Int) {
                fun bar(x: Int) {
                    when (x) {
                        1 -> throw IOException()
                        2 -> throw IOException()
                        3 -> throw IOException()
                    }
                }
                return bar(x)
            }
        """.trimIndent()
        val subject = ThrowsCount(Config.empty)

        @Test
        fun `reports violation by default`() {
            val findings = subject.lint(code)
            assertThat(findings).singleElement()
                .hasStartSourceLocation(4, 9)
        }
    }

    @Nested
    inner class `max count == 3` {
        val code = """
            import java.io.IOException

            fun f4(x: String?) {
                val denulled = x ?: throw IOException()
                val int = x?.toInt() ?: throw IOException()
                val double = x?.toDouble() ?: throw IOException()
            }
        """.trimIndent()

        @Test
        fun `does not report when max parameter is 3`() {
            val config = TestConfig(MAX to "3")
            val subject = ThrowsCount(config)
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports violation when max parameter is 2`() {
            val config = TestConfig(MAX to "2")
            val subject = ThrowsCount(config)
            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `code with ELVIS operator guard clause` {
        val codeWithGuardClause = """
            fun test(x: Int): Int {
                val y = x ?: throw Exception()
                when (x) {
                    5 -> println("x=5")
                    4 -> throw Exception()
                }
                throw Exception()
            }
        """.trimIndent()

        @Test
        fun `should not report violation with EXCLUDE_GUARD_CLAUSES as true`() {
            val config = TestConfig(EXCLUDE_GUARD_CLAUSES to "true")
            val subject = ThrowsCount(config)
            assertThat(subject.lint(codeWithGuardClause)).isEmpty()
        }

        @Test
        fun `should report violation with EXCLUDE_GUARD_CLAUSES as false`() {
            val config = TestConfig(EXCLUDE_GUARD_CLAUSES to "false")
            val subject = ThrowsCount(config)
            assertThat(subject.lint(codeWithGuardClause)).hasSize(1)
        }
    }

    @Nested
    inner class `code with if condition guard clause` {
        val codeWithGuardClause = """
            fun test(x: Int): Int {
                if(x == null) throw Exception()
                when (x) {
                    5 -> println("x=5")
                    4 -> throw Exception()
                }
                throw Exception()
            }
        """.trimIndent()

        @Test
        fun `should not report violation with EXCLUDE_GUARD_CLAUSES as true`() {
            val config = TestConfig(EXCLUDE_GUARD_CLAUSES to "true")
            val subject = ThrowsCount(config)
            assertThat(subject.lint(codeWithGuardClause)).isEmpty()
        }

        @Test
        fun `should report violation with EXCLUDE_GUARD_CLAUSES as false`() {
            val config = TestConfig(EXCLUDE_GUARD_CLAUSES to "false")
            val subject = ThrowsCount(config)
            assertThat(subject.lint(codeWithGuardClause)).hasSize(1)
        }
    }

    @Nested
    inner class `reports a too-complicated if statement for being a guard clause` {
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
        """.trimIndent()

        @Test
        fun `should report violation even with EXCLUDE_GUARD_CLAUSES as true`() {
            val config = TestConfig(EXCLUDE_GUARD_CLAUSES to "true")
            val subject = ThrowsCount(config)
            assertThat(subject.lint(codeWithIfCondition)).hasSize(1)
        }
    }

    @Nested
    inner class `a file with 2 returns and an if condition guard clause which is not the first statement` {
        val codeWithIfCondition = """
            fun test(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> throw Exception()
                }
                if (x < 4) throw Exception()
                throw Exception()
            }
        """.trimIndent()

        @Test
        fun `should report the violation even with EXCLUDE_GUARD_CLAUSES as true`() {
            val config = TestConfig(EXCLUDE_GUARD_CLAUSES to "true")
            val subject = ThrowsCount(config)
            assertThat(subject.lint(codeWithIfCondition)).hasSize(1)
        }
    }

    @Nested
    inner class `a file with 2 returns and an ELVIS guard clause which is not the first statement` {
        val codeWithIfCondition = """
            fun test(x: Int): Int {
                when (x) {
                    5 -> println("x=5")
                    4 -> throw Exception()
                }
                val y = x ?: throw Exception()
                throw Exception()
            }
        """.trimIndent()

        @Test
        fun `should report the violation even with EXCLUDE_GUARD_CLAUSES as true`() {
            val config = TestConfig(EXCLUDE_GUARD_CLAUSES to "true")
            val subject = ThrowsCount(config)
            assertThat(subject.lint(codeWithIfCondition)).hasSize(1)
        }
    }

    @Nested
    inner class `a file with multiple guard clauses` {
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
        """.trimIndent()

        @Test
        fun `should not report violation with EXCLUDE_GUARD_CLAUSES as true`() {
            val config = TestConfig(EXCLUDE_GUARD_CLAUSES to "true")
            val subject = ThrowsCount(config)
            assertThat(subject.lint(codeWithMultipleGuardClauses)).isEmpty()
        }

        @Test
        fun `should report violation with EXCLUDE_GUARD_CLAUSES as false`() {
            val config = TestConfig(EXCLUDE_GUARD_CLAUSES to "false")
            val subject = ThrowsCount(config)
            assertThat(subject.lint(codeWithMultipleGuardClauses)).hasSize(1)
        }
    }
}
