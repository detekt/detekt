package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BracesOnIfStatementsSpec {

    private fun createSubject(singleLine: String, multiLine: String): BracesOnIfStatements {
        val config = TestConfig(
            mapOf(
                "singleLine" to singleLine,
                "multiLine" to multiLine
            )
        )
        return BracesOnIfStatements(config)
    }

    @Nested
    inner class `consistency checks` {
        val subject = createSubject("consistent", "consistent")

        @Test
        fun `does not report consistent multi-line with else if`() {
            val findings = subject.compileAndLint(
                """
                    fun f() {
                        if (true) { 
                            println() 
                        } else if (true) println()
                        if (true) println() 
                        else if (true) println()
                    }
                """.trimIndent()
            )

            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report consistent single-line with else if`() {
            val findings = subject.compileAndLint(
                """
                    fun f() {
                        if (true) { println() } else if (true) println()
                        if (true) { println() } else { if (true) println() }
                        if (true) println() else if (true) println()
                    }
                """.trimIndent()
            )

            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report consistent single line if`() {
            val findings = subject.compileAndLint(
                """
                    fun f() {
                        if (true) println() else println()
                        if (true) { println() } else { println() }
                    }
                """.trimIndent()
            )

            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report consistent multi-line if`() {
            val findings = subject.compileAndLint(
                """
                    fun f() {
                        if (true) { 
                            println()
                        } else {
                            println()
                        }
                        if (true) println()
                        else println()
                    }
                """.trimIndent()
            )

            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports inconsistent single line if`() {
            val findings = subject.compileAndLint(
                """
                    fun f() {
                        if (true) { println() } else println()
                        if (true) println() else { println() }
                    }
                """.trimIndent()
            )

            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(14 to 16, 57 to 59)
        }

        @Test
        fun `reports inconsistent multi line if`() {
            val findings = subject.compileAndLint(
                """
                    fun f() {
                        if (true) {
                            println() 
                        } else println()
                        if (true) 
                            println()
                        else { println() }
                    }
                """.trimIndent()
            )

            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(14 to 16, 70 to 72)
        }
    }

    @Nested
    inner class `if statements which should have braces` {
        val subject = createSubject("never", "always")

        @Test
        fun `reports either then or else without braces`() {
            val findings = subject.compileAndLint(
                """
                    fun f() {
                        if (true) { println() } 
                        else println()
                        
                        if (true) println() 
                        else { println() }
                    }
                """.trimIndent()
            )

            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(43 to 47, 67 to 69)
        }

        @Test
        fun `reports a simple if`() {
            val findings = subject.compileAndLint(
                """
            fun f() {
                if (true)
                    println()
            }
                """.trimIndent()
            )

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(14 to 16)
        }

        @Test
        fun `reports a simple if with a single statement in multiple lines`() {
            val findings = subject.compileAndLint(
                """
                fun f() {
                	if (true) 50
                        .toString()
                }
                """.trimIndent()
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
                """.trimIndent()
            )

            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(11 to 13, 44 to 48)
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
                """.trimIndent()
            )

            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(14 to 16, 46 to 50)
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
                """.trimIndent()
            )

            assertThat(findings).hasSize(3)
            assertThat(findings).hasTextLocations(14 to 16, 51 to 53, 84 to 88)
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
                """.trimIndent()
            )

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(50 to 54)
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
                """.trimIndent()
            )

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(14 to 16)
        }

        @Test
        fun `reports both then and else 1`() {
            val findings = subject.compileAndLint(
                """
            fun f() {
                if (true) println()
                else println()
            }
                """.trimIndent()
            )

            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(14 to 16, 38 to 42)
        }

        @Test
        fun `reports both then and else 2`() {
            val findings = subject.compileAndLint(
                """
            fun f() {
                if (true) println() else
                    println()
            }
                """.trimIndent()
            )

            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(14 to 16, 34 to 38)
        }
    }

    @Nested
    inner class `multiline if statements with braces` {
        val subject = createSubject("never", "always")

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
                	if (true)                 
                		{ println() }
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `single-line if statements which don't need braces` {
        val subject = createSubject("never", "always")

        @Test
        fun `does not report single-line if statements`() {
            val code = """
                fun f() {
                	if (true) println()
                	if (true) println() else println()
                	if (true) println() else if (false) println() else println()
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }

    @Nested
    inner class `multi-line when following an else statement without requiring braces` {
        val subject = createSubject("never", "always")

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
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}
