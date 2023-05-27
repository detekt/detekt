package io.gitlab.arturbosch.detekt.rules

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.core.rules.visitFile
import io.gitlab.arturbosch.detekt.rules.complexity.ComplexCondition
import io.gitlab.arturbosch.detekt.rules.complexity.LongMethod
import io.gitlab.arturbosch.detekt.rules.complexity.LongParameterList
import io.gitlab.arturbosch.detekt.rules.complexity.StringLiteralDuplication
import io.gitlab.arturbosch.detekt.rules.complexity.TooManyFunctions
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SuppressingSpec {

    @Test
    fun `all findings are suppressed on element levels`() {
        @Suppress("KotlinConstantConditions")
        val code = """
            @SuppressWarnings("LongParameterList")
            fun lpl(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) = (a + b + c + d + e + f).apply {
                assert(false) { "FAILED TEST" }
            }
            
            @SuppressWarnings("ComplexCondition")
            class SuppressedElements {
            
                @SuppressWarnings("LongParameterList")
                fun lpl(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) = (a + b + c + d + e + f).apply {
                    assert(false) { "FAILED TEST" }
                }
            
                @SuppressWarnings("ComplexCondition")
                fun cc() {
                    if (this is SuppressedElements && this !is Any && this is Nothing && this is SuppressedElements) {
                        assert(false) { "FAIL" }
                    }
                }
            
                @SuppressWarnings("LongMethod")
                fun lm() {
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    assert(false) { "FAILED TEST" }
                }
            
            }
        """.trimIndent()
        val ktFile = compileContentForTest(code)
        val ruleSet = RuleSet("Test", listOf(LongMethod(), LongParameterList(), ComplexCondition()))

        val findings = ruleSet.visitFile(ktFile)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `all findings are suppressed on file levels`() {
        @Suppress("KotlinConstantConditions")
        val code = """
            @file:Suppress("LongMethod", "LongParameterList", "ComplexCondition")
            
            fun lpl2(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) = (a + b + c + d + e + f).apply {
                assert(false) { "FAILED TEST" }
            }
            
            class SuppressedElements2 {
            
                fun lpl(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) = (a + b + c + d + e + f).apply {
                    assert(false) { "FAILED TEST" }
                }
            
                fun cc() {
                    if (this is SuppressedElements2 && this !is Any && this is Nothing && this is SuppressedElements2) {
                        assert(false) { "FAIL" }
                    }
                }
            
                fun lm() {
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    assert(false) { "FAILED TEST" }
                }
            }
        """.trimIndent()
        val ktFile = compileContentForTest(code)
        val ruleSet = RuleSet("Test", listOf(LongMethod(), LongParameterList(), ComplexCondition()))

        val findings = ruleSet.visitFile(ktFile)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `all findings are suppressed on class levels`() {
        @Suppress("KotlinConstantConditions")
        val code = """
            @Suppress("LongMethod", "LongParameterList", "ComplexCondition")
            class SuppressedElements3 {
            
                fun lpl(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) = (a + b + c + d + e + f).apply {
                    assert(false) { "FAILED TEST" }
                }
            
                fun cc() {
                    if (this is SuppressedElements3 && this !is Any && this is Nothing && this is SuppressedElements3) {
                        assert(false) { "FAIL" }
                    }
                }
            
                fun lm() {
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    lpl(1, 2, 3, 4, 5, 6)
                    assert(false) { "FAILED TEST" }
                }
            }
        """.trimIndent()

        val ktFile = compileContentForTest(code)
        val ruleSet = RuleSet("Test", listOf(LongMethod(), LongParameterList(), ComplexCondition()))

        val findings = ruleSet.visitFile(ktFile)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should suppress TooManyFunctionsRule on class level`() {
        val rule = TooManyFunctions(TestConfig("thresholdInClasses" to "0"))
        val code = """
            @Suppress("TooManyFunctions")
            class OneIsTooMany {
                fun f() {}
            }
        """.trimIndent()

        val findings = rule.lint(code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should suppress StringLiteralDuplication on class level`() {
        @Suppress("UnusedEquals")
        val code = """
            @Suppress("StringLiteralDuplication")
            class Duplication {
                var s1 = "lorem"
                fun f(s: String = "lorem") {
                    s1 == "lorem"
                }
            }
        """.trimIndent()

        assertThat(StringLiteralDuplication().lint(code)).isEmpty()
    }
}
