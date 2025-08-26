package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Finding
import dev.detekt.api.TextLocation
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import io.gitlab.arturbosch.detekt.rules.style.BracesOnWhenStatements.BracePolicy
import io.gitlab.arturbosch.detekt.rules.style.BracesOnWhenStatementsSpec.Companion.NOT_RELEVANT
import io.gitlab.arturbosch.detekt.rules.style.BracesOnWhenStatementsSpec.Companion.options
import io.gitlab.arturbosch.detekt.rules.style.BracesOnWhenStatementsSpec.Companion.test
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.ThrowingConsumer
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * Note: this class makes extensive use of dynamic tests and containers, few tips for maintenance:
 *  * Numbers are made relative to the code snippet rather than the whole code passed to Kotlin compiler. [test].
 *  * To debug a specific test case, remove all other test cases from the same method.
 *  * Test coverage is added for all possible configuration options, see [NOT_RELEVANT].
 */
@Suppress(
    "ClassName",
    "LongMethod",
    "CommentOverPrivateProperty"
)
class BracesOnWhenStatementsSpec {

    @Test
    fun `validate behavior of occurrence function`() {
        val code = "fun f() { if (true) else if (true) if (true) true }"
        assertThat("if"(1)(code)).isEqualTo(10 to 12)
        assertThat("if"(2)(code)).isEqualTo(25 to 27)
        assertThat("if"(3)(code)).isEqualTo(35 to 37)
        assertThat("else"(1)(code)).isEqualTo(20 to 24)
        assertThatCode { "if"(4)(code) }.isInstanceOf(IllegalArgumentException::class.java)
        assertThatCode { "if"(0)(code) }.isInstanceOf(IllegalArgumentException::class.java)
        assertThatCode { "else"(2)(code) }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Nested
    inner class specialTestCases {
        @TestFactory
        fun `special when conditions`() = flag(
            """
                when (0) {
                    1, 2 -> { println() }
                    in 3..4 -> { println() }
                    is Int -> { println() }
                    5, 6, in 7..8, is Number -> { println() }
                    else -> { println() }
                }
            """.trimIndent(),
            "->"(1),
            "->"(2),
            "->"(3),
            "->"(4),
            "->"(5),
        )

        private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
            val validTopLevelCode = """
                fun main() {
                    $code
                }
            """.trimIndent()
            return testCombinations(BracePolicy.Never.config, BracePolicy.Always.config, validTopLevelCode, *locations)
        }

        @TestFactory
        fun `extra line break between branches`() =
            flag(
                """
                    when (1) {

                        1 -> println()

                        2 -> println()

                        else -> println()

                    }
                """.trimIndent(),
                *NOTHING
            )

        @TestFactory
        fun `nested when inside when case block`() =
            flag(
                """
                    when (1) {
                        1 -> { 
                            when (2) { 1 -> println(); else -> { println() } }
                        }
                        2 -> println()
                        else -> { println() }
                    }
                """.trimIndent(),
                "->"(3),
                "->"(4),
            )

        @TestFactory
        fun `nested when inside when subject`() =
            flag(
                """
                    when (when (2) { 1 -> { 1 }; else -> { 2 } }) {
                        1 -> {
                            println()
                        }
                        2 -> println()
                        else -> { println() }
                    }
                """.trimIndent(),
                "->"(1),
                "->"(2),
                "->"(4),
            )

        @TestFactory
        fun `nested when inside when condition`() =
            flag(
                """
                    fun f(s: String?) {
                        when {
                            when { s != null -> { s.length }; else -> { 0 } } > 5 -> {
                                true
                            }
                            when(s) { "foo" -> { 1 }; "bar" -> { 1 }; else -> { 2 } } == 1 -> false
                            else -> { null }
                        }
                    }
                """.trimIndent(),
                "->"(1),
                "->"(2),
                "->"(4),
                "->"(5),
                "->"(6),
                "->"(7),
            )

        @TestFactory
        fun `weird curly formatting for multiline whens`() =
            flag(
                """
                    when (1) {
                        1 -> 
                        {
                            println()
                        }
                        2 -> { println()
                        }
                        3 -> { 
                        println() }
                        else -> 
                            { println() }
                    }
                """.trimIndent(),
                *NOTHING,
            )
    }

    @Nested
    inner class singleLine {

        @Nested
        inner class `=always` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(BracePolicy.Always.config, NOT_RELEVANT, validTopLevelCode, *locations)
            }

            @TestFactory
            fun `missing braces are flagged`() =
                flag(
                    """
                        when (1) {
                            1 -> println()
                            2 -> { println(); println() }
                        }
                    """.trimIndent(),
                    "->"(1),
                )

            @TestFactory
            fun `existing braces are accepted`() =
                flag(
                    """
                        when (1) {
                            1 -> { println() }
                            2 -> { println() }
                            else -> { println(); println() }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory
            fun `partially missing braces are flagged (first branch)`() =
                flag(
                    """
                        when (1) {
                            1 -> println()
                            2 -> { println() }
                            else -> { println(); println() }
                        }
                    """.trimIndent(),
                    "->"(1),
                )

            @TestFactory
            fun `partially missing braces are flagged (last branch)`() =
                flag(
                    """
                        when (1) {
                            1 -> { println() }
                            2 -> { println(); println() }
                            else -> println()
                        }
                    """.trimIndent(),
                    "->"(3),
                )

            @TestFactory
            fun `partially missing braces are flagged (middle branches)`() =
                flag(
                    """
                        when (1) {
                            1 -> { println(); println() }
                            2 -> println()
                            else -> { println() }
                        }
                    """.trimIndent(),
                    "->"(2),
                )
        }

        @Nested
        inner class `=never` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(BracePolicy.Never.config, NOT_RELEVANT, validTopLevelCode, *locations)
            }

            @TestFactory fun `no braces are accepted`() =
                flag(
                    """
                        when (1) {
                            1 -> println()
                            2 -> println()
                            else -> println()
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory
            fun `existing braces are flagged`() =
                flag(
                    """
                        when (1) {
                            1 -> { println() }
                            2 -> { println() }
                            else -> { println(); println() }
                        }
                    """.trimIndent(),
                    "->"(1),
                    "->"(2),
                    "->"(3),
                )

            @TestFactory
            fun `partially extra braces are flagged (first branch)`() =
                flag(
                    """
                        when (1) {
                            1 -> { println(); println() }
                            2 -> println()
                            else -> println()
                        }
                    """.trimIndent(),
                    "->"(1),
                )

            @TestFactory
            fun `partially extra braces are flagged (last branch)`() =
                flag(
                    """
                        when (1) {
                            1 -> println()
                            2 -> println()
                            else -> { println() }
                        }
                    """.trimIndent(),
                    "->"(3),
                )

            @TestFactory
            fun `partially extra braces are flagged (middle branches)`() =
                flag(
                    """
                        when (1) {
                            1 -> println()
                            2 -> { println() }
                            else -> println()
                        }
                    """.trimIndent(),
                    "->"(2),
                )

            @TestFactory fun `empty blocks are accepted`() =
                flag(
                    """
                        fun test(i: Int) {
                            when {
                                i == 1 -> println(1)
                                i == 2 -> {}
                                i == 3 -> {}
                                else -> println(4)
                            }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory fun `necessary braces for lambda expression are accepted`() =
                flag(
                    """
                        fun test(bool: Boolean) {
                            val f: () -> Int = when {
                                bool -> { { 1 } }
                                else -> { { 2 } }
                            }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory fun `unnecessary braces for lambda expression are flagged`() =
                flag(
                    """
                        fun test(bool: Boolean) {
                            val f: () -> Int = when {
                                bool -> { { -> 1 } }
                                else -> { { -> 2 } }
                            }
                        }
                    """.trimIndent(),
                    "->"(2),
                    "->"(4),
                )
        }

        @Nested
        inner class `=necessary` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(BracePolicy.Necessary.config, NOT_RELEVANT, validTopLevelCode, *locations)
            }

            @TestFactory
            fun `no braces are accepted`() =
                flag(
                    """
                        when (1) {
                            1 -> println()
                            2 -> println()
                            else -> { println(); println() }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory
            fun `existing braces are flagged`() =
                flag(
                    """
                        when (1) {
                            1 -> { println() }
                            2 -> { println() }
                            else -> { println() }
                        }
                    """.trimIndent(),
                    "->"(1),
                    "->"(2),
                    "->"(3),
                )

            @TestFactory
            fun `partially extra braces are flagged (first branch)`() =
                flag(
                    """
                        when (1) {
                            1 -> { println() }
                            2 -> println()
                            else -> { println(); println() }
                        }
                    """.trimIndent(),
                    "->"(1),
                )

            @TestFactory
            fun `partially extra braces are flagged (last branch)`() =
                flag(
                    """
                        when (1) {
                            1 -> { println(); println() }
                            2 -> println()
                            else -> { println() }
                        }
                    """.trimIndent(),
                    "->"(3),
                )

            @TestFactory
            fun `partially extra braces are flagged (middle branches)`() =
                flag(
                    """
                        when (1) {
                            1 -> println()
                            2 -> { println() }
                            else -> { println(); println() }
                        }
                    """.trimIndent(),
                    "->"(2),
                )

            @TestFactory fun `empty blocks are accepted`() =
                flag(
                    """
                        fun test(i: Int) {
                            when {
                                i == 1 -> println(1)
                                i == 2 -> {}
                                i == 3 -> {}
                                else -> println(4)
                            }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory fun `necessary braces for lambda expression are accepted`() =
                flag(
                    """
                        fun test(bool: Boolean) {
                            val f: () -> Int = when {
                                bool -> { { 1 } }
                                else -> { { 2 } }
                            }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory fun `unnecessary braces for lambda expression are flagged`() =
                flag(
                    """
                        fun test(bool: Boolean) {
                            val f: () -> Int = when {
                                bool -> { { -> 1 } }
                                else -> { { -> 2 } }
                            }
                        }
                    """.trimIndent(),
                    "->"(2),
                    "->"(4),
                )
        }

        @Nested
        inner class `=consistent` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(BracePolicy.Consistent.config, NOT_RELEVANT, validTopLevelCode, *locations)
            }

            @TestFactory
            fun `no braces are accepted`() =
                flag(
                    """
                        when (1) {
                            1 -> println()
                            2 -> println()
                            else -> println()
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory
            fun `existing braces are accepted`() =
                flag(
                    """
                        when (1) {
                            1 -> { println() }
                            2 -> { println() }
                            else -> { println() }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory
            fun `partial braces are flagged (first branch)`() =
                flag(
                    """
                        when (1) {
                            1 -> println()
                            2 -> { println() }
                            else -> { println() }
                        }
                    """.trimIndent(),
                    "when"(1),
                )

            @TestFactory
            fun `partial braces are flagged (last branch)`() =
                flag(
                    """
                        when (1) {
                            1 -> { println() }
                            2 -> { println() }
                            else -> println()
                        }
                    """.trimIndent(),
                    "when"(1),
                )

            @TestFactory
            fun `partial braces are flagged (middle branches)`() =
                flag(
                    """
                        when (1) {
                            1 -> { println() }
                            2 -> println()
                            else -> { println() }
                        }
                    """.trimIndent(),
                    "when"(1),
                )

            @TestFactory fun `empty blocks are accepted`() =
                flag(
                    """
                        fun test(i: Int) {
                            when {
                                i == 1 -> println(1)
                                i == 2 -> {}
                                i == 3 -> {}
                                else -> println(4)
                            }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory fun `necessary braces for lambda expression are accepted`() =
                flag(
                    """
                        fun test(bool: Boolean) {
                            val f: () -> Int = when {
                                bool -> { { 1 } }
                                else -> { -> 2 }
                            }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory fun `unnecessary braces for lambda expression are flagged`() =
                flag(
                    """
                        fun test(bool: Boolean) {
                            val f: () -> Int = when {
                                bool -> { -> 1 }
                                else -> { { -> 2 } }
                            }
                        }
                    """.trimIndent(),
                    "when"(1),
                )
        }
    }

    @Nested
    inner class multiLine {

        @Nested
        inner class `=always` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(NOT_RELEVANT, BracePolicy.Always.config, validTopLevelCode, *locations)
            }

            @TestFactory
            fun `missing braces are flagged`() =
                flag(
                    """
                        when (1) {
                            1 -> 
                                println()
                            2 -> { println() }
                            else -> {
                                println()
                                println()
                            }
                        }
                    """.trimIndent(),
                    "->"(1),
                )

            @TestFactory
            fun `existing braces are accepted`() =
                flag(
                    """
                        when (1) {
                            1 -> { 
                                println()
                            }
                            2 -> { println() }
                            else -> {
                                println()
                                println()
                            }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory
            fun `partially missing braces are flagged (first branch)`() =
                flag(
                    """
                        when (1) {
                            1 -> { 
                                println()
                            }
                            2 -> println()
                            else -> {
                                println()
                                println()
                            }
                        }
                    """.trimIndent(),
                    "->"(2),
                )

            @TestFactory
            fun `partially missing braces are flagged (last branch)`() =
                flag(
                    """
                        when (1) {
                            1 -> { 
                                println()
                            }
                            2 -> {
                                println()
                                println()
                            }
                            else -> 
                                println()
                        }
                    """.trimIndent(),
                    "->"(3),
                )

            @TestFactory
            fun `partially missing braces are flagged (middle branches)`() =
                flag(
                    """
                        when (1) {
                            1 -> { 
                                println()
                            }
                            2 ->
                                println()
                            else -> { 
                                println()
                                println()
                            }
                        }
                    """.trimIndent(),
                    "->"(2),
                )
        }

        @Nested
        inner class `=never` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(NOT_RELEVANT, BracePolicy.Never.config, validTopLevelCode, *locations)
            }

            @TestFactory
            fun `no braces are accepted`() =
                flag(
                    """
                        when (1) {
                            1 ->
                                println()
                            2 ->
                                println()
                            else ->
                                println()
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory
            fun `existing braces are flagged`() =
                flag(
                    """
                        when (1) {
                            1 -> {
                                println()
                            }
                            2 -> {
                                println()
                            }
                            else -> {
                                println()
                            }
                        }
                    """.trimIndent(),
                    "->"(1),
                    "->"(2),
                    "->"(3),
                )

            @TestFactory
            fun `partially extra braces are flagged (first branch)`() =
                flag(
                    """
                        when (1) {
                            1 -> {
                                println()
                            }
                            2 ->
                                println()
                            
                            else ->
                                println()
                        }
                    """.trimIndent(),
                    "->"(1),
                )

            @TestFactory
            fun `partially extra braces are flagged (last branch)`() =
                flag(
                    """
                        when (1) {
                            1 ->
                                println()
                            2 ->
                                println()
                            else -> {
                                println()
                                println()
                            }
                        }
                    """.trimIndent(),
                    "->"(3),
                )

            @TestFactory
            fun `partially extra braces are flagged (middle branches)`() =
                flag(
                    """
                        when (1) {
                            1 ->
                                println()
                            
                            2 -> {
                                println()
                                println()
                            }
                            
                            else ->
                                println()
                        }
                    """.trimIndent(),
                    "->"(2),
                )

            @TestFactory fun `empty blocks are accepted`() =
                flag(
                    """
                        fun test(i: Int) {
                            when {
                                i == 1 -> 
                                    println(1)
                                i == 2 -> {
                                }
                                i == 3 -> {
                                }
                                else ->
                                    println(4)
                            }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory fun `necessary braces for lambda expression are accepted`() =
                flag(
                    """
                        fun test(bool: Boolean) {
                            val f: () -> Int = when {
                                bool -> {
                                    { 1 }
                                }
                                else -> {
                                    { 2 }
                                }
                            }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory fun `unnecessary braces for lambda expression are flagged`() =
                flag(
                    """
                        fun test(bool: Boolean) {
                            val f: () -> Int = when {
                                bool -> {
                                    { -> 1 }
                                }
                                else -> {
                                    { -> 2 }
                                }
                            }
                        }
                    """.trimIndent(),
                    "->"(2),
                    "->"(4),
                )
        }

        @Nested
        inner class `=necessary` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(NOT_RELEVANT, BracePolicy.Necessary.config, validTopLevelCode, *locations)
            }

            @TestFactory
            fun `no braces are accepted`() =
                flag(
                    """
                        when (1) {
                            1 ->
                                println()
                            2 ->
                                println()
                            else ->
                                println()
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory
            fun `existing braces are flagged`() =
                flag(
                    """
                        when (1) {
                            1 -> {
                                println()
                            }
                            2 -> {
                                println()
                            }
                            else -> {
                                println()
                            }
                        }
                    """.trimIndent(),
                    "->"(1),
                    "->"(2),
                    "->"(3),
                )

            @TestFactory
            fun `partially extra braces are flagged (first branch)`() =
                flag(
                    """
                        when (1) {
                            1 -> {
                                println()
                            }
                            2 -> {
                                println()
                                println()
                            }
                            else -> {
                                println()
                                println()
                            }
                        }
                    """.trimIndent(),
                    "->"(1),
                )

            @TestFactory
            fun `partially extra braces are flagged (last branch)`() =
                flag(
                    """
                        when (1) {
                            1 -> {
                                println()
                                println()
                            }
                            2 -> {
                                println()
                                println()
                            }
                            else -> {
                                println()
                            }
                        }
                    """.trimIndent(),
                    "->"(3),
                )

            @TestFactory
            fun `partially extra braces are flagged (middle branches)`() =
                flag(
                    """
                        when (1) {
                            1 -> {
                                println()
                                println()
                            }
                            2 -> {
                                println()
                            }
                            else ->
                                println()
                            
                        }
                    """.trimIndent(),
                    "->"(2),
                )

            @TestFactory
            fun `existing braces are not flagged when necessary`() =
                flag(
                    """
                        when (1) {
                            1 -> {
                                println()
                                println()
                            }
                            2 -> {
                                println()
                                println()
                            }
                            else -> {
                                println()
                                println()
                            }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory fun `empty blocks are accepted`() =
                flag(
                    """
                        fun test(i: Int) {
                            when {
                                i == 1 -> 
                                    println(1)
                                i == 2 -> {
                                }
                                i == 3 -> {
                                }
                                else ->
                                    println(4)
                            }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory fun `necessary braces for lambda expression are accepted`() =
                flag(
                    """
                        fun test(bool: Boolean) {
                            val f: () -> Int = when {
                                bool -> {
                                    { 1 }
                                }
                                else -> {
                                    { 2 }
                                }
                            }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory fun `unnecessary braces for lambda expression are flagged`() =
                flag(
                    """
                        fun test(bool: Boolean) {
                            val f: () -> Int = when {
                                bool -> {
                                    { -> 1 }
                                }
                                else -> {
                                    { -> 2 }
                                }
                            }
                        }
                    """.trimIndent(),
                    "->"(2),
                    "->"(4),
                )
        }

        @Nested
        inner class `=consistent` {

            private fun flag(code: String, vararg locations: (String) -> Pair<Int, Int>): DynamicNode {
                val validTopLevelCode = """
                    fun main() {
                        $code
                    }
                """.trimIndent()
                return testCombinations(NOT_RELEVANT, BracePolicy.Consistent.config, validTopLevelCode, *locations)
            }

            @TestFactory
            fun `no braces are accepted`() =
                flag(
                    """
                        when (1) {
                            1 ->
                                println()
                            2 ->
                                println()
                            else ->
                                println()
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory
            fun `existing braces are accepted`() =
                flag(
                    """
                        when (1) {
                            1 -> { 
                                println()
                            }
                            2 -> { println() }
                            else -> {
                                println()
                                println()
                            }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory
            fun `inconsistent braces are flagged`() =
                flag(
                    """
                        when (1) {
                            1 -> {
                                println()
                            }
                            2 -> {
                                println()
                            }
                            else ->
                                println()
                        }
                    """.trimIndent(),
                    "when"(1),
                )

            @TestFactory
            fun `inconsistent braces are flagged (first branch)`() =
                flag(
                    """
                        when (1) {
                            1 -> {
                                println()
                            }
                            2 ->
                                println()
                            else ->
                                println()
                        }
                    """.trimIndent(),
                    "when"(1),
                )

            @TestFactory
            fun `inconsistent braces are flagged (last branch)`() =
                flag(
                    """
                        when (1) {
                            1 -> {
                                println()
                            }
                            2 -> {
                                println()
                            }
                            else ->
                                println()
                        }
                    """.trimIndent(),
                    "when"(1),
                )

            @TestFactory
            fun `inconsistent braces are flagged (middle branches)`() =
                flag(
                    """
                        when (1) {
                            1 -> {
                                println()
                            }
                            2 -> println()
                            else -> {
                                println()
                            }
                        }
                    """.trimIndent(),
                    "when"(1),
                )

            @TestFactory fun `empty blocks are accepted`() =
                flag(
                    """
                        fun test(i: Int) {
                            when {
                                i == 1 -> 
                                    println(1)
                                i == 2 -> {
                                }
                                i == 3 -> {
                                }
                                else -> 
                                    println(4)
                            }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory fun `necessary braces for lambda expression are accepted`() =
                flag(
                    """
                        fun test(bool: Boolean) {
                            val f: () -> Int = when {
                                bool -> { 
                                    { 1 }
                                }
                                else ->
                                    { -> 2 }
                            }
                        }
                    """.trimIndent(),
                    *NOTHING,
                )

            @TestFactory fun `unnecessary braces for lambda expression are flagged`() =
                flag(
                    """
                        fun test(bool: Boolean) {
                            val f: () -> Int = when {
                                bool ->
                                    { -> 1 }
                                else -> {
                                    { -> 2 }
                                }
                            }
                        }
                    """.trimIndent(),
                    "when"(1),
                )
        }
    }

    companion object {

        /**
         * Not relevant means, that it should be covered, but for that specific test case the value doesn't matter.
         * This needs to be covered still, to make sure it never becomes relevant.
         * In this rule, configuration options should be separate for each config, not coupled with each other.
         */
        private const val NOT_RELEVANT: String = "*"

        /**
         * Nothing is expected to be flagged as a finding.
         */
        private val NOTHING: Array<(String) -> Pair<Int, Int>> = emptyArray()

        private fun createSubject(singleLine: String, multiLine: String): BracesOnWhenStatements {
            val config = TestConfig(
                "singleLine" to singleLine,
                "multiLine" to multiLine
            )
            return BracesOnWhenStatements(config)
        }

        private fun options(option: String): List<String> =
            if (option == NOT_RELEVANT) {
                BracePolicy.entries.map { it.config }
            } else {
                listOf(option)
            }

        private fun testCombinations(
            singleLine: String,
            multiLine: String,
            code: String,
            vararg locations: (String) -> Pair<Int, Int>,
        ): DynamicNode {
            val codeLocation = locations.map { it(code) }.toTypedArray()
            // Separately compile the code because otherwise all the combinations would compile them again and again.
            val compileTest = dynamicTest("Compiles: $code") {
                BracesOnWhenStatements(Config.empty).lint(code)
            }
            val validationTests = createBraceTests(singleLine, multiLine) { rule ->
                rule.test(code, *codeLocation)
            }
            val locationString = if (NOTHING.contentEquals(codeLocation)) {
                "nothing"
            } else {
                codeLocation.map { TextLocation(it.first, it.second) }.toString()
            }
            return dynamicContainer("flags $locationString in `$code`", validationTests + compileTest)
        }

        private fun BracesOnWhenStatements.test(code: String, vararg locations: Pair<Int, Int>) {
            // This creates a 10 character prefix (signature/9, space/1) for every code example.
            // Note: not compileAndLint for performance reasons, compilation is in a separate test.
            val findings = lint("fun f() { $code }", compile = false)
            // Offset text locations by the above prefix, it results in 0-indexed locations.
            val offset = 10
            val textPositions = locations.map { it.first + offset to it.second + offset }
            assertThat(findings).satisfiesExactlyInAnyOrder(
                *textPositions
                    .map { textPosition -> ThrowingConsumer<Finding> { assertThat(it).hasTextLocation(textPosition) } }
                    .toTypedArray()
            )
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
            test: (BracesOnWhenStatements) -> Unit,
        ): List<DynamicNode> {
            val singleOptions = options(singleLine)
            val multiOptions = options(multiLine)
            require(singleOptions.isNotEmpty() && multiOptions.isNotEmpty()) {
                "No options to test: $singleLine -> $singleOptions, $multiLine -> $multiOptions"
            }
            return singleOptions.flatMap { singleLineOption ->
                multiOptions.map { multiLineOption ->
                    val trace = Exception("Async stack trace of TestFactory")
                    dynamicTest("singleLine=$singleLineOption, multiLine=$multiLineOption") {
                        try {
                            // Note: if you jumped here from a failed test's stack trace,
                            // select the "Async stack trace of TestFactory" cause's
                            // last relevant stack line to jump to the actual test code.
                            test(createSubject(singleLineOption, multiLineOption))
                        } catch (e: Throwable) {
                            generateSequence(e) { it.cause }.last().initCause(trace)
                            throw e
                        }
                    }
                }
            }
        }

        operator fun String.invoke(ordinal: Int): (String) -> Pair<Int, Int> =
            { code ->
                fun String.next(string: String, start: Int): Int? = indexOf(string, start).takeIf { it != -1 }

                val indices = generateSequence(code.next(this, 0)) { startIndex ->
                    code.next(this, startIndex + 1)
                }
                val index = requireNotNull(indices.elementAtOrNull(ordinal - 1)) {
                    "There's no $ordinal. occurrence of '$this' in '$code'"
                }
                index to index + this.length
            }
    }
}
