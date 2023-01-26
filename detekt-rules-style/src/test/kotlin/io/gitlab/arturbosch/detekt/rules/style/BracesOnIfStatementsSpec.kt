@file:Suppress("ClassName", "PrivatePropertyName", "ClassOrdering", "VariableNaming")

package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory

private val NO_BRACES_SINGLE: String = """
    if (true) println()
    if (true) println() else println()
    if (true) println() else if println()
    if (true) println() else if println() else println()
    if (true) println() else if println() else if println() else println()
""".trimIndent()

private val NO_BRACES_SINGLE_LOCATIONS: Array<Pair<Int, Int>> = arrayOf(
    14 to 16,
    38 to 40,
    58 to 62,
    77 to 79,
    102 to 104,
    119 to 121,
    144 to 146,
    157 to 161,
    176 to 178,
    201 to 203,
    219 to 221,
    232 to 236,
)

private val ALL_BRACES_SINGLE: String = """
    if (true) { println() }
    if (true) { println() } else { println() }
    if (true) { println() } else if { println() }
    if (true) { println() } else if { println() } else { println() }
    if (true) { println() } else if { println() } else if { println() } else { println() }
""".trimIndent()

private val ALL_BRACES_SINGLE_LOCATIONS: Array<Pair<Int, Int>> = arrayOf(
    14 to 16,
    42 to 44,
    66 to 70,
    89 to 91,
    118 to 120,
    139 to 141,
    168 to 170,
    185 to 189,
    208 to 210,
    237 to 239,
    259 to 261,
    276 to 280,
)

private val IF_BRACES_SINGLE: String = """
    if (true) { println() }
    if (true) { println() } else println()
    if (true) { println() } else if println()
    if (true) { println() } else if println() else println()
    if (true) { println() } else if println() else if println() else println()
""".trimIndent()

private val IF_BRACES_SINGLE_BRACE_LOCATIONS: Array<Pair<Int, Int>> = arrayOf(
    14 to 16,
    42 to 44,
    85 to 87,
    131 to 133,
    192 to 194,
)

private val IF_BRACES_SINGLE_NO_BRACE_LOCATIONS: Array<Pair<Int, Int>> = arrayOf(
    66 to 70,
    114 to 116,
    160 to 162,
    173 to 177,
    221 to 223,
    239 to 241,
    252 to 256,
)

private val ELSE_BRACES_SINGLE: String = """
    if (true) println()
    if (true) println() else { println() }
    if (true) println() else if println()
    if (true) println() else if println() else { println() }
    if (true) println() else if println() else if println()
    if (true) println() else if println() else if println() else { println() }
""".trimIndent()

private const val NOT_RELEVANT = "*"

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

    private fun BracesOnIfStatements.compileAndLintInF(code: String): List<Finding> =
        compileAndLint(
            """
                fun f() {
                    ${code.prependIndent("                    ").trimStart()}
                }
            """.trimIndent()
        )

    @Nested
    inner class singleLine {
//
//        @Nested
//        inner class `=always` {
//
//            private val singleLine = BracesOnIfStatements.BracePolicy.Always.config
//
//            @TestFactory
//            fun `missing braces are flagged`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(NO_BRACES_SINGLE)
//
//                assertThat(findings).hasTextLocations(*NO_BRACES_SINGLE_LOCATIONS)
//            }
//
//            @TestFactory
//            fun `existing braces are accepted`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(ALL_BRACES_SINGLE)
//
//                assertThat(findings).isEmpty()
//            }
//
//            @TestFactory
//            fun `partially missing braces are flagged`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(IF_BRACES_SINGLE)
//
//                assertThat(findings).hasTextLocations(*IF_BRACES_SINGLE_NO_BRACE_LOCATIONS)
//            }
//        }

        @Nested
        inner class `=never` {

            val NOTHING = arrayOf<Pair<Int, Int>>()
            private val singleLine = BracesOnIfStatements.BracePolicy.Never.config

//            @TestFactory
//            fun `no braces are accepted`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(NO_BRACES_SINGLE)
//
//                assertThat(findings).isEmpty()
//            }
//
//            @TestFactory
//            fun `existing braces are flagged`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(ALL_BRACES_SINGLE)
//
//                assertThat(findings).hasTextLocations(*ALL_BRACES_SINGLE_LOCATIONS)
//            }

            context(BracesOnIfStatements)
            private fun MutableList<DynamicTest>.flag(code: String, vararg locations: Pair<Int, Int>) {
                add(
                    dynamicTest("flags ${locations.map { TextLocation(it.first, it.second) }} in `$code`") {
                        val findings = compileAndLintInF(code)

                        assertThat(findings).hasTextLocations(*locations.map { it.first + 13 to it.second + 13 }
                            .toTypedArray())
                    }
                )
            }

            @TestFactory
            fun `partially extra braces are flagged`() = braceTests("never", NOT_RELEVANT) {
                flag("if (true) { println() }", 1 to 3)
                flag("if (true) { println() } else println()", 1 to 3)
                flag("if (true) { println() } else if (true) println()", 1 to 3)
                flag("if (true) { println() } else if (true) println() else println()", 1 to 3)
                flag(
                    """
                        if (true) { println() } else if (true) println() else if (true) println() else println()
                    """.trimIndent(),
                    1 to 3,
                )
            }

            @TestFactory
            fun `partially missing braces are flagged`() = braceTests("always", NOT_RELEVANT) {
                flag("if (true) { println() }", *NOTHING)
                flag("if (true) { println() } else println()", 25 to 29)
                flag("if (true) { println() } else if (true) println()", 30 to 32)
                flag("if (true) { println() } else if (true) println() else println()", 30 to 32, 50 to 52)
                flag(
                    """
                        if (true) { println() } else if (true) println() else if (true) println() else println()
                    """.trimIndent(),
                    30 to 32, 50 to 52, 75 to 79,
                )
            }
        }
//
//        @Nested
//        inner class `=necessary` {
//
//            private val singleLine = BracesOnIfStatements.BracePolicy.Necessary.config
//
//            @TestFactory
//            fun `no braces are accepted`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(NO_BRACES_SINGLE)
//
//                assertThat(findings).isEmpty()
//            }
//
//            @TestFactory
//            fun `existing braces are flagged`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(ALL_BRACES_SINGLE)
//
//                assertThat(findings).hasTextLocations(*ALL_BRACES_SINGLE_LOCATIONS)
//            }
//
//            @TestFactory
//            fun `partially extra braces are flagged`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(IF_BRACES_SINGLE)
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
//                val findings = compileAndLintInF(NO_BRACES_SINGLE)
//
//                assertThat(findings).isEmpty()
//            }
//
//            @TestFactory
//            fun `existing braces are flagged`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(ALL_BRACES_SINGLE)
//
//                assertThat(findings).isEmpty()
//            }
//
//            @TestFactory
//            fun `partial braces are flagged`() = braceTests(singleLine, NOT_RELEVANT) {
//                val findings = compileAndLintInF(IF_BRACES_SINGLE)
//
//                assertThat(findings).hasTextLocations(*IF_BRACES_SINGLE_BRACE_LOCATIONS.drop(1).toTypedArray())
//            }
//        }
    }

//    @TestFactory
//    fun `whens are not flagged`() = braceTests(NOT_RELEVANT, NOT_RELEVANT) {
//        val findings = compileAndLintInF(
//            """
//                when (true) {
//                    true -> println()
//                    else -> println()
//                }
//            """.trimIndent()
//        )
//
//        assertThat(findings).isEmpty()
//    }

    private fun braceTests(
        singleLine: String,
        multiLine: String,
        test: context(BracesOnIfStatements) MutableList<DynamicTest>.() -> Unit
    ): List<DynamicNode> {
        val singleOptions = options(singleLine)
        val multiOptions = options(multiLine)
        return when {
            singleOptions.size > 1 && multiOptions.size > 1 ->
                singleOptions.flatMap { singleLineOption ->
                    multiOptions.map { multiLineOption ->
                        val tests = mutableListOf<DynamicTest>()
                        test(createSubject(singleLineOption, multiLineOption), tests)
                        dynamicContainer("singleLine=${singleLineOption}, multiLine=${multiLineOption}", tests)
                    }
                }

            singleOptions.size > 1 && multiOptions.size == 1 ->
                singleOptions.map { singleLineOption ->
                    val tests = mutableListOf<DynamicTest>()
                    test(createSubject(singleLineOption, multiOptions.single()), tests)
                    dynamicContainer("singleLine=${singleLineOption}, multiLine=${multiOptions.single()}", tests)
                }

            singleOptions.size == 1 && multiOptions.size > 1 ->
                multiOptions.map { multiLineOption ->
                    val tests = mutableListOf<DynamicTest>()
                    test(createSubject(singleOptions.single(), multiLineOption), tests)
                    dynamicContainer("singleLine=${singleOptions.single()}, multiLine=${multiLineOption}", tests)
                }

            else ->
                error("No options to test: $singleLine -> $singleOptions, $multiLine -> $multiOptions")
        }
    }

    private fun options(option: String): List<String> =
        if (option == NOT_RELEVANT)
            BracesOnIfStatements.BracePolicy.values().map { it.config }
        else
            listOf(option)
}
