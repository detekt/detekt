package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat as assertThatJ

@KotlinCoreEnvironmentTest
class AvoidDollarLiteralInterpolationSpec(val env: KotlinEnvironmentContainer) {
    private val subject = AvoidDollarLiteralInterpolation(Config.empty)

    @Test
    fun `reports direct dollar interpolation in regular and raw strings`() {
        val code = """
            val regularString = "DOLLAR{"DOLLAR"}Hello"
            val regularCharacter = "DOLLAR{'DOLLAR'}Hello"
            val regularEscapedString = "DOLLAR{"BACKSLASHDOLLAR"}Hello"
            val rawString = TRIPLE_QUOTEDOLLAR{"DOLLAR"}HelloTRIPLE_QUOTE
            val rawCharacter = TRIPLE_QUOTEDOLLAR{'DOLLAR'}HelloTRIPLE_QUOTE
            val rawEscapedString = TRIPLE_QUOTEDOLLAR{"BACKSLASHDOLLAR"}HelloTRIPLE_QUOTE
        """.trimIndent().asKotlinCode()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings).hasSize(6)
    }

    @Test
    fun `reports simple and braced references to immutable dollar values`() {
        val code = """
            const val topLevelDollar = "DOLLAR"
            val chainedDollar = topLevelDollar

            fun test() {
                val localDollar = 'DOLLAR'
                val regularSimple = "DOLLARlocalDollar Hello"
                val regularBraced = "DOLLAR{localDollar}Hello"
                val rawSimple = TRIPLE_QUOTEDOLLARchainedDollar HelloTRIPLE_QUOTE
                val rawBraced = TRIPLE_QUOTEDOLLAR{chainedDollar}HelloTRIPLE_QUOTE
            }
        """.trimIndent().asKotlinCode()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings).hasSize(4)
    }

    @Test
    fun `does not report escaped dollar signs in regular strings`() {
        val code = """
            val greeting = "BACKSLASHDOLLARHello"
        """.trimIndent().asKotlinCode()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report interpolated string expressions that are not a dollar sign`() {
        val code = """
            val currency = "USD"
            val interpolated = "DOLLAR{"DOLLARcurrency"}"
            val escaped = "DOLLAR{"BACKSLASHn"}"
        """.trimIndent().asKotlinCode()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports unnecessary interpolation in strings that are already multi-dollar`() {
        val code = """
            val dollar = "DOLLAR"
            val regularDirect = DOLLARDOLLAR"DOLLARDOLLAR{"DOLLAR"}Hello"
            val regularReference = DOLLARDOLLAR"DOLLARDOLLARdollar Hello"
            val rawDirect = DOLLARDOLLARTRIPLE_QUOTEDOLLARDOLLAR{'DOLLAR'}HelloTRIPLE_QUOTE
            val rawReference = DOLLARDOLLARTRIPLE_QUOTEDOLLARDOLLAR{dollar}HelloTRIPLE_QUOTE
        """.trimIndent().asKotlinCode()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings).hasSize(4)
        assertThat(findings).allMatch {
            it.message.endsWith("Express it directly using the existing multi-dollar interpolation prefix.")
        }
    }

    @Test
    fun `recommends replacements based on the string context`() {
        val code = """
            val regular = "DOLLAR{"DOLLAR"}Hello"
            val raw = TRIPLE_QUOTEDOLLAR{"DOLLAR"}HelloTRIPLE_QUOTE
            val multiDollar = DOLLARDOLLAR"DOLLARDOLLAR{"DOLLAR"}Hello"
        """.trimIndent().asKotlinCode()

        val messages = subject.lintWithContext(env, code).map { it.message }

        assertThatJ(messages[0]).endsWith("Escape it or use multi-dollar interpolation to express it directly.")
        assertThatJ(messages[1]).endsWith("Use multi-dollar interpolation to express it directly.")
        assertThatJ(messages[2])
            .endsWith("Express it directly using the existing multi-dollar interpolation prefix.")
    }

    @Test
    fun `does not report values that are not statically known to be a dollar sign`() {
        val code = """
            var mutableDollar = "DOLLAR"
            val delegatedDollar by lazy { "DOLLAR" }
            open class Currency {
                open val openDollar = "DOLLAR"
                val customDollar get() = "DOLLAR"
            }

            class CurrencyFromParameter(val dollar: String) {
                fun display() = "DOLLARdollar"
            }

            abstract class AbstractCurrency {
                abstract val dollar: String
                fun display() = "DOLLARdollar"
            }

            fun currencySymbol() = "DOLLAR"

            fun test(currency: Currency) {
                val computedDollar = currencySymbol()
                val other = "USD"
                println("DOLLARmutableDollar Hello")
                println("DOLLARdelegatedDollar Hello")
                println("DOLLAR{currency.openDollar}Hello")
                println("DOLLAR{currency.customDollar}Hello")
                println("DOLLARcomputedDollar Hello")
                println("DOLLARother Hello")
            }
        """.trimIndent().asKotlinCode()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports each dollar interpolation in the same string`() {
        val code = """
            val greeting = "DOLLAR{'DOLLAR'}Hello DOLLAR{"DOLLAR"}World"
        """.trimIndent().asKotlinCode()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings).hasSize(2)
    }

    private fun String.asKotlinCode(): String =
        replace("TRIPLE_QUOTE", "\"\"\"")
            .replace("BACKSLASH", "\\")
            .replace("DOLLAR", "$")
}
