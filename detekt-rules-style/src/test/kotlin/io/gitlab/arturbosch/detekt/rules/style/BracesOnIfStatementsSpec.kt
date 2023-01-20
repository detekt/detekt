package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Language("kotlin")
private val BLOCK_WITHOUT_BRACES_PASS = """
    fun f() { 
        if (true) println()

        if (true) 
            println()

        if (true) println() else println()

        if (true) 
            println() 
        else
            println()

        if (true) println() else if (true) println() else println()

        if (true) 
            println() 
        else if (true)
            println() 
        else
            println()

        when (true) {
            true -> if (true) println() else println()
            true -> if (true) 
                    println() 
                else 
                    println()
            true -> if (true) println() else if (true) println() else println()
            else -> if (true) 
                    println() 
                else if (true)
                    println()
                else
                    println()
        }
    }
""".trimIndent()

@Language("kotlin")
private val BLOCK_WITH_BRACES_PASS = """
    fun f() { 
        if (true) { println() }

        if (true) {
            println()
        }

        if (true) { println() } else { println() }

        if (true) {
            println()
        } else { 
            println()
        }

        if (true) { println() } else if (true) { println() } else { println() }

        if (true) { println() } else { if (true) { println() } else { println() } }

        if (true) { 
            println()
        } else if (true) { 
            println() 
        } else { 
            println() 
        }

        when (true) {
            true -> if (true) { println() } else { println() }
            true -> if (true) {
                    println()
                } else { 
                    println()
                }
            true -> if (true) { println() } else if (true) { println() } else { println() }
            else -> if (true) {
                    println()
                } else if (true) {
                    println()
                } else {
                    println()
                }
        }
    }
""".trimIndent()

@Language("kotlin")
private val BLOCK_WITHOUT_BRACES_FAIL = """
    fun f() { 
        if (true) { println() } else println()

        if (true) 
            println() 
        else {
            println()
        }

        if (true) println() else if (true) println() else { println() }

        if (true) 
            println() 
        else if (true) {
            println()
        } else
            println()

        when (true) {
            true -> if (true) println() else { println() }
            true -> if (true) {
                    println() 
                } else 
                    println()
            true -> if (true) println() else if (true) { println() } else println()
            else -> if (true) 
                    println() 
                else if (true)
                    println()
                else {
                    println()
                }
        }
    }
""".trimIndent()

@Language("kotlin")
private val BLOCK_WITH_BRACES_FAIL = """
    fun f() { 
        if (true) println() else { println() }

        if (true) {
            println()
        } else
            println()

        if (true) { println() } else if (true) println() else { println() }

        if (true) println() else { if (true) println() else println() }

        if (true) { 
            println()
        } else if (true) { 
            println() 
        } else 
            println()

        when (true) {
            true -> if (true) { println() } else println()
            true -> if (true) println()
                else { 
                    println()
                }
            true -> if (true) { println() } else if (true) { println() } else println()
            else -> if (true) {
                    println()
                } else if (true)
                    println()
                else {
                    println()
                }
        }
    }
""".trimIndent()

@Language("kotlin")
private val BLOCK_NECESSARY_BRACES_PASS = """
    fun f() { 
        if (true) println() else if (true) { println(); println() } else println()

        if (true) 
            println() 
        else if (true) {
            println()
            println() 
        } else
            println()
    }
""".trimIndent()

@Language("kotlin")
private val BLOCK_NECESSARY_BRACES_FAIL = """
    fun f() { 
        if (true) println() else if (true) { println(); println() } else { println() }

        if (true) 
            println() 
        else if (true) {
            println()
            println() 
        } else {
            println()
        }
    }
""".trimIndent()

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
    inner class `braces always` {
        val subject = createSubject("always", "always")

        @Test
        fun `does not report correct block with braces`() {
            val findings = subject.compileAndLint(BLOCK_WITH_BRACES_PASS)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports incorrect block with braces`() {
            val findings = subject.compileAndLint(BLOCK_WITH_BRACES_FAIL)

            assertThat(findings).hasTextLocations(
                15 to 17,
                95 to 99,
                152 to 154,
                223 to 225,
                243 to 247,
                196 to 198,
                345 to 349,
                428 to 432,
                459 to 461,
                608 to 612,
                696 to 698
            )
        }
    }

    @Nested
    inner class `braces consistent` {
        val subject = createSubject("consistent", "consistent")

        @Test
        fun `does not report consistent block without braces`() {
            val findings = subject.compileAndLint(BLOCK_WITHOUT_BRACES_PASS)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report consistent block with braces`() {
            val findings = subject.compileAndLint(BLOCK_WITH_BRACES_PASS)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports inconsistent block without braces`() {
            val findings = subject.compileAndLint(BLOCK_WITHOUT_BRACES_FAIL)

            assertThat(findings).hasTextLocations(
                15 to 17,
                59 to 61,
                129 to 131,
                198 to 200,
                331 to 333,
                386 to 388,
                487 to 489,
                567 to 569
            )
        }

        @Test
        fun `reports inconsistent block with braces`() {
            val findings = subject.compileAndLint(BLOCK_WITH_BRACES_FAIL)

            assertThat(findings).hasTextLocations(
                15 to 17,
                59 to 61,
                123 to 125,
                196 to 198,
                265 to 267,
                404 to 406,
                459 to 461,
                555 to 557,
                639 to 641
            )
        }

        @Test
        fun `reports if when else is multi-statement`() {
            val findings = subject.compileAndLint(
                """
                    fun f() {
                        if (true) println() else { println(); println() }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasTextLocations(14 to 16)
        }
    }

    @Nested
    inner class `braces never` {
        val subject = createSubject("never", "never")

        @Test
        fun `does not report correct block without braces`() {
            val findings = subject.compileAndLint(BLOCK_WITHOUT_BRACES_PASS)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports incorrect block without braces`() {
            val findings = subject.compileAndLint(BLOCK_WITHOUT_BRACES_FAIL)

            assertThat(findings).hasTextLocations(
                15 to 17,
                93 to 97,
                174 to 178,
                237 to 239,
                351 to 355,
                386 to 388,
                512 to 514,
                670 to 674
            )
        }
    }

    @Nested
    inner class `braces necessary` {
        val subject = createSubject("necessary", "necessary")

        @Test
        fun `does not report correct block without braces`() {
            val findings = subject.compileAndLint(BLOCK_NECESSARY_BRACES_PASS)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports incorrect block without braces`() {
            val findings = subject.compileAndLint(BLOCK_NECESSARY_BRACES_FAIL)

            assertThat(findings).hasTextLocations(
                75 to 79,
                193 to 197
            )
        }
    }

    @Nested
    inner class `mixed policy` {
        val subject = createSubject("never", "always")

        @Test
        fun `does not report correct block`() {
            val findings = subject.compileAndLint(
                """
                    fun f() {
                        if (true) println() else println()

                        if (true) { 
                            println()
                        } else {
                            println()
                        }
                    }
                
                """.trimIndent()
            )

            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports incorrect block`() {
            val findings = subject.compileAndLint(
                """
                    fun f() {
                        if (true) println() else { println() }

                        if (true) { 
                            println()
                        } else
                            println()
                    }
                
                """.trimIndent()
            )

            assertThat(findings).hasTextLocations(
                34 to 38,
                95 to 99
            )
        }
    }
}
