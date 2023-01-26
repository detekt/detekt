@file:Suppress("ClassName", "CommentOverPrivateProperty")

package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.rules.style.BracesOnIfStatements.BracePolicy
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory

/**
 * Not relevant means, that it should be covered, but for that specific test case the value doesn't matter.
 * This needs to be covered still, to make sure it never becomes relevant.
 * In this rule, configuration options should be separate for each config, not coupled with each other.
 */
private const val NOT_RELEVANT: String = "*"

/**
 * Nothing is expected to be flagged as a finding.
 */
private val NOTHING: Array<Pair<Int, Int>> = emptyArray()

/**
 * Note: this class makes extensive use of dynamic tests and containers, few tips for maintenance:
 *  * Numbers are made relative to the code snippet rather than the whole code passed to Kotlin compiler. [compileAndLintInF].
 *  * To debug a specific test case, remove all other test cases from the same method.
 *  * Test coverage is added for all possible configuration options, see [NOT_RELEVANT].
 */
class BracesOnIfStatementsSpec {

    @Nested
    inner class singleLine {

        @Nested
        inner class `=always` {

            private fun flag(code: String, vararg locations: Pair<Int, Int>) =
                testCombinations(BracePolicy.Always.config, NOT_RELEVANT, code, *locations)

            @TestFactory
            fun `missing braces are flagged`() = listOf(
                flag("if (true) println()", 0 to 2),
                flag("if (true) println() else println()", 0 to 2),
                flag("if (true) println() else if (true) println()", 0 to 2),
                flag("if (true) println() else if (true) println() else println()", 0 to 2),
                flag("if (true) println() else if println() else if (true) println() else println()", 0 to 2),
            )

            @TestFactory
            fun `existing braces are accepted`() = listOf(
                flag("if (true) { println() }", *NOTHING),
                flag("if (true) { println() } else { println() }", *NOTHING),
                flag("if (true) { println() } else if { println() }", *NOTHING),
                flag("if (true) { println() } else if { println() } else { println() }", *NOTHING),
                flag(
                    "if (true) { println() } else if { println() } else if { println() } else { println() }",
                    *NOTHING
                ),
            )

            @TestFactory
            fun `partially missing braces are flagged`() = listOf(
                flag("if (true) { println() }"),
                flag("if (true) { println() } else println()"),
                flag("if (true) { println() } else if println()"),
                flag("if (true) { println() } else if println() else println()"),
                flag("if (true) { println() } else if println() else if println() else println()"),
            )
        }

        @Nested
        inner class `=never` {

            private fun flag(code: String, vararg locations: Pair<Int, Int>) =
                testCombinations(BracePolicy.Never.config, NOT_RELEVANT, code, *locations)

            @TestFactory fun `no braces are accepted`() = listOf(
                flag("if (true) println()", *NOTHING),
                flag("if (true) println() else println()", *NOTHING),
                flag("if (true) println() else if (true) println()", *NOTHING),
                flag("if (true) println() else if (true) println() else println()", *NOTHING),
                flag("if (true) println() else if println() else if (true) println() else println()", *NOTHING),
            )

            @TestFactory
            fun `existing braces are flagged`() = listOf(
                flag("if (true) { println() }"),
                flag("if (true) { println() } else { println() }"),
                flag("if (true) { println() } else if (true) { println() }"),
                flag("if (true) { println() } else if (true) { println() } else { println() }"),
                flag("if (true) { println() } else if (true) { println() } else if (true) { println() } else { println() }"),
            )

            @TestFactory fun `partially extra braces are flagged`() = listOf(
                flag("if (true) { println() }", 0 to 2),
                flag("if (true) { println() } else println()", 0 to 2),
                flag("if (true) { println() } else if (true) println()", 0 to 2),
                flag("if (true) { println() } else if (true) println() else println()", 0 to 2),
                flag(
                    """
                        if (true) { println() } else if (true) println() else if (true) println() else println()
                    """.trimIndent(),
                    0 to 2,
                ),
            )
        }

//        @Nested
//        inner class `=necessary` {
//
//            private val singleLine = BracesOnIfStatements.BracePolicy.Necessary.config
//
//            @TestFactory
//            fun `no braces are accepted`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(
//                    """
//                        if (true) println()
//                        if (true) println() else println()
//                        if (true) println() else if println()
//                        if (true) println() else if println() else println()
//                        if (true) println() else if println() else if println() else println()
//                    """.trimIndent()
//                )
//
//                assertThat(findings).isEmpty()
//            }
//
//            @TestFactory
//            fun `existing braces are flagged`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(
//                    """
//                        if (true) { println() }
//                        if (true) { println() } else { println() }
//                        if (true) { println() } else if { println() }
//                        if (true) { println() } else if { println() } else { println() }
//                        if (true) { println() } else if { println() } else if { println() } else { println() }
//                    """.trimIndent()
//                )
//
//                assertThat(findings).hasTextLocations(*ALL_BRACES_SINGLE_LOCATIONS)
//            }
//
//            @TestFactory
//            fun `partially extra braces are flagged`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(
//                    """
//                        if (true) { println() }
//                        if (true) { println() } else println()
//                        if (true) { println() } else if println()
//                        if (true) { println() } else if println() else println()
//                        if (true) { println() } else if println() else if println() else println()
//                    """.trimIndent()
//                )
//
//                assertThat(findings).hasTextLocations(*IF_BRACES_SINGLE_BRACE_LOCATIONS)
//            }
//        }
//
//        @Nested
//        inner class `=consistent` {
//
//            private val singleLine = BracesOnIfStatements.BracePolicy.Consistent.config
//
//            @TestFactory
//            fun `no braces are accepted`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(
//                    """
//                        if (true) println()
//                        if (true) println() else println()
//                        if (true) println() else if println()
//                        if (true) println() else if println() else println()
//                        if (true) println() else if println() else if println() else println()
//                    """.trimIndent()
//                )
//
//                assertThat(findings).isEmpty()
//            }
//
//            @TestFactory
//            fun `existing braces are flagged`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(
//                    """
//                        if (true) { println() }
//                        if (true) { println() } else { println() }
//                        if (true) { println() } else if { println() }
//                        if (true) { println() } else if { println() } else { println() }
//                        if (true) { println() } else if { println() } else if { println() } else { println() }
//                    """.trimIndent()
//                )
//
//                assertThat(findings).isEmpty()
//            }
//
//            @TestFactory
//            fun `partial braces are flagged`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(
//                    """
//                        if (true) { println() }
//                        if (true) { println() } else println()
//                        if (true) { println() } else if println()
//                        if (true) { println() } else if println() else println()
//                        if (true) { println() } else if println() else if println() else println()
//                    """.trimIndent()
//                )
//
//                assertThat(findings).hasTextLocations(*IF_BRACES_SINGLE_BRACE_LOCATIONS.drop(1).toTypedArray())
//            }
//        }
    }

    @TestFactory
    fun `whens are not flagged`() = testCombinations(
        NOT_RELEVANT,
        NOT_RELEVANT,
        """
            when (true) {
                true -> println()
                else -> println()
            }
        """.trimIndent(),
        *NOTHING
    )

    private fun testCombinations(
        singleLine: String,
        multiLine: String,
        code: String,
        vararg locations: Pair<Int, Int>
    ): DynamicNode {
        val tests = createBraceTests(singleLine, multiLine) { rule ->
            // This creates a 14 character prefix (signature/9, newline/1, indent/4) for every code example.
            val findings = rule.compileAndLint(
                """
                    fun f() {
                        ${code.prependIndent("                        ").trimStart()}
                    }
                """.trimIndent()
            )
            // Offset text locations by the above prefix, it results in 0-indexed locations.
            assertThat(findings).hasOffsetTextLocations(14, *locations)
        }
        val locationString = if (NOTHING.contentEquals(locations))
            "nothing"
        else
            locations.map { TextLocation(it.first, it.second) }.toString()
        return dynamicContainer("flags $locationString in `$code`", tests)
    }

    /**
     * Generates a list of tests for the given brace policy combinations.
     * The expectations in the test will be the same for all combinations.
     *
     * @see options for how the arguments are interpreted.
     */
    private fun createBraceTests(
        singleLine: String,
        multiLine: String,
        test: (BracesOnIfStatements) -> Unit
    ): List<DynamicNode> {
        val singleOptions = options(singleLine)
        val multiOptions = options(multiLine)
        require(singleOptions.isNotEmpty() && multiOptions.isNotEmpty()) {
            "No options to test: ${singleLine} -> ${singleOptions}, ${multiLine} -> ${multiOptions}"
        }
        return singleOptions.flatMap { singleLineOption ->
            multiOptions.map { multiLineOption ->
                val trace = Exception("Stack trace of TestFactory")
                dynamicTest("singleLine=${singleLineOption}, multiLine=${multiLineOption}") {
                    try {
                        // Note: if you jumped here from a failed test's stack trace,
                        // select the "Stack trace of TestFactory" cause's last relevant line to jump to the actual test.
                        test(createSubject(singleLineOption, multiLineOption))
                    } catch (e: Throwable) {
                        generateSequence(e) { it.cause }.last().initCause(trace)
                        throw e
                    }
                }
            }
        }
    }

    private fun createSubject(singleLine: String, multiLine: String): BracesOnIfStatements {
        val config = TestConfig(
            mapOf(
                "singleLine" to singleLine,
                "multiLine" to multiLine
            )
        )
        return BracesOnIfStatements(config)
    }

    private fun options(option: String): List<String> =
        if (option == NOT_RELEVANT)
            BracePolicy.values().map { it.config }
        else
            listOf(option)
}
