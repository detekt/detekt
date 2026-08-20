package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseMultiDollarInterpolationSpec(val env: KotlinEnvironmentContainer) {
    private val subject = UseMultiDollarInterpolation(Config.empty)

    @Test
    fun `reports direct dollar interpolation in regular and raw strings`() {
        val code = """
            val regularString = "DOLLAR{"DOLLAR"}Hello"
            val regularCharacter = "DOLLAR{'DOLLAR'}Hello"
            val rawString = TRIPLE_QUOTEDOLLAR{"DOLLAR"}HelloTRIPLE_QUOTE
            val rawCharacter = TRIPLE_QUOTEDOLLAR{'DOLLAR'}HelloTRIPLE_QUOTE
        """.trimIndent().asKotlinCode()

        val findings = subject.lintWithContext(env, code)

        assertThat(findings).hasSize(4)
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
    fun `does not report interpolation that is already multi-dollar`() {
        val code = """
            val dollar = "DOLLAR"
            val greeting = DOLLARDOLLAR"DOLLARDOLLARdollar Hello"
            val rawGreeting = DOLLARDOLLARTRIPLE_QUOTEDOLLARDOLLARdollar HelloTRIPLE_QUOTE
        """.trimIndent().asKotlinCode()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
