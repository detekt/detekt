package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Language("kotlin")
private const val BLOCK_WITHOUT_BRACES_PASS = """
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
        else 
            if (true) println() else println()

        val a = 0
        when (a) {
            0 -> if (true) println() else println()
            1 -> if (true) 
                    println() 
                else 
                    println()
            2 -> if (true) println() else if (true) println() else println()
            3 -> if (true) 
                    println() 
                else 
                    if (true) println() else println()
        }
    }
"""

@Language("kotlin")
private const val BLOCK_WITH_BRACES_PASS = """
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

        val a = 0
        when (a) {
            0 -> if (true) { println() } else { println() }
            1 -> if (true) {
                    println()
                } else { 
                    println()
                }
            2 -> if (true) { println() } else if (true) { println() } else { println() }
            3 -> if (true) {
                    println()
                } else if (true) {
                    println()
                } else {
                    println()
                }
        }
    }
"""

@Language("kotlin")
private const val BLOCK_WITHOUT_BRACES_FAIL = """
    fun f() { 
        if (true) println() 
        if (true) 
            println() 
        if (true) { println() } else println() 
        if (true) 
            println() 
        else {
            println()
        }

        if (true) println() else if (true) println() else { println() }
        if (true) 
            println() 
        else 
            if (true) { println() } else println()

        val a = 0
        when (a) {
            0 -> if (true) println() else { println() }
            1 -> if (true) 
                    { println() } 
                else 
                    println()
            2 -> if (true) println() else if (true) { println() } else println()
            3 -> if (true) 
                    println() 
                else 
                    if (true) println() else { println() }
        }
    }
"""

@Language("kotlin")
private const val BLOCK_WITH_BRACES_FAIL = """
    fun f() { 
        if (true) { println() } 
        if (true) {
            println()
        }
        if (true) println() else { println() } 
        if (true) {
            println()
        } else println()

        if (true) { println() } else if (true) println() else { println() }
        if (true) println() else { if (true) println() else println() }
        if (true) { 
            println()
        } else if (true) { 
            println() 
        } else 
            println()

        val a = 0
        when (a) {
            0 -> if (true) { println() } else println()
            1 -> if (true) println()
                else { 
                    println()
                }
            2 -> if (true) { println() } else if (true) { println() } else println()
            3 -> if (true) {
                    println()
                } else if (true)
                    println()
                else {
                    println()
                }
        }
    }
"""

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

            assertThat(findings).hasSize(8)
            assertThat(findings).hasTextLocations(
                119 to 123,
                185 to 189,
                278 to 282,
                365 to 367,
                479 to 483,
                577 to 581,
                655 to 657,
                815 to 819
            )
        }

        @Test
        fun `reports inconsistent block with braces`() {
            val findings = subject.compileAndLint(BLOCK_WITH_BRACES_FAIL)

            assertThat(findings).hasSize(9)
            assertThat(findings).hasTextLocations(
                129 to 133,
                201 to 205,
                254 to 256,
                321 to 325,
                469 to 473,
                576 to 580,
                644 to 648,
                770 to 774,
                867 to 869
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
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(14 to 16)
        }
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

            assertThat(findings).hasSize(10)
            assertThat(findings).hasTextLocations(
                109 to 111,
                201 to 205,
                254 to 256,
                328 to 330,
                301 to 303,
                469 to 473,
                576 to 580,
                608 to 610,
                770 to 774,
                867 to 869
            )
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

            assertThat(findings).hasSize(8)
            assertThat(findings).hasTextLocations(
                95 to 97,
                185 to 189,
                278 to 282,
                365 to 367,
                479 to 483,
                515 to 517,
                655 to 657,
                815 to 819
            )
        }
    }
}
