package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MandatoryBracesIfStatementsSpec {
    val subject = MandatoryBracesIfStatements(Config.empty)

    @Nested
    inner class `if statements which should have braces` {

        @Test
        fun `reports a simple if`() {
            val findings = subject.compileAndLint(
                """
            fun f() {
                if (true)
                    println()
            }
                """
            )

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(32 to 41)
        }

        @Test
        fun `reports a simple if with a single statement in multiple lines`() {
            val findings = subject.compileAndLint(
                """
                fun f() {
                	if (true) 50
                        .toString()
                }
                """
            )

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports if-else with a single statement in multiple lines`() {
            val findings = subject.compileAndLint(
                """
                fun f() {
                	if (true) 50
                        .toString() else 50
                        .toString()
                }
                """
            )

            assertThat(findings).hasSize(2)
        }

        @Test
        fun `reports if-else`() {
            val findings = subject.compileAndLint(
                """
            fun f() {
                if (true)
                    println()
                else
                    println()
            }
                """
            )

            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(32 to 41, 59 to 68)
        }

        @Test
        fun `reports if-else with else-if`() {
            val findings = subject.compileAndLint(
                """
            fun f() {
                if (true)
                    println()
                else if (false)
                    println()
                else
                    println()
            }
                """
            )

            assertThat(findings).hasSize(3)
            assertThat(findings).hasTextLocations(32 to 41, 70 to 79, 97 to 106)
        }

        @Test
        fun `reports if with braces but else without`() {
            val findings = subject.compileAndLint(
                """
            fun f() {
                if (true) {
                    println()
                } else
                    println()
            }
                """
            )

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(63 to 72)
        }

        @Test
        fun `reports else with braces but if without`() {
            val findings = subject.compileAndLint(
                """
            fun f() {
                if (true)
                    println()
                else {
                    println()
                }
            }
                """
            )

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(32 to 41)
        }

        @Test
        fun `reports else in new line`() {
            val findings = subject.compileAndLint(
                """
            fun f() {
                if (true) println()
                else println()
            }
                """
            )

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(24 to 33)
        }

        @Test
        fun `reports only else body on new line`() {
            val findings = subject.compileAndLint(
                """
            fun f() {
                if (true) println() else
                    println()
            }
                """
            )

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(47 to 56)
        }
    }

    @Nested
    inner class `if statements with braces` {

        @Test
        fun `does not report if statements with braces`() {
            val code = """
                fun f() {
                	if (true) {
                		println()
                	}
                	if (true)
                	{
                		println()
                	}
                	if (true) { println() }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `single-line if statements which don't need braces` {

        @Test
        fun `does not report single-line if statements`() {
            val code = """
                fun f() {
                	if (true) println()
                	if (true) println() else println()
                	if (true) println() else if (false) println() else println()
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `multi-line when following an else statement without requiring braces` {

        @Test
        fun `does not report multi-line when`() {
            val code = """
                fun f(i: Int) {
                	if (true) {
                        println()
                    } else when(i) {
                        1 -> println(1)
                        else -> println()
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}
