package io.gitlab.arturbosch.detekt.rules

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.complexity.ComplexCondition
import io.gitlab.arturbosch.detekt.rules.complexity.LongMethod
import io.gitlab.arturbosch.detekt.rules.complexity.LongParameterList
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SuppressingSpec {

    @Test
    @Suppress("LongMethod")
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

        val findings = listOf(
            LongMethod(Config.empty),
            LongParameterList(Config.empty),
            ComplexCondition(Config.empty),
        ).flatMap { it.visitFile(ktFile) }

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

        val findings = listOf(
            LongMethod(Config.empty),
            LongParameterList(Config.empty),
            ComplexCondition(Config.empty),
        ).flatMap { it.visitFile(ktFile) }

        assertThat(findings).isEmpty()
    }
}
