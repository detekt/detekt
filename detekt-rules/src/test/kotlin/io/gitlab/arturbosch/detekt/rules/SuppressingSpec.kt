package io.gitlab.arturbosch.detekt.rules

import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
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
        val ktFile = compileForTest(resourceAsPath("SuppressedByElementAnnotation.kt"))
        val ruleSet = RuleSet("Test", listOf(LongMethod(), LongParameterList(), ComplexCondition()))

        val findings = ruleSet.visitFile(ktFile)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `all findings are suppressed on file levels`() {
        val ktFile = compileForTest(resourceAsPath("SuppressedElementsByFileAnnotation.kt"))
        val ruleSet = RuleSet("Test", listOf(LongMethod(), LongParameterList(), ComplexCondition()))

        val findings = ruleSet.visitFile(ktFile)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `all findings are suppressed on class levels`() {
        val ktFile = compileForTest(resourceAsPath("SuppressedElementsByClassAnnotation.kt"))
        val ruleSet = RuleSet("Test", listOf(LongMethod(), LongParameterList(), ComplexCondition()))

        val findings = ruleSet.visitFile(ktFile)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should suppress TooManyFunctionsRule on class level`() {
        val rule = TooManyFunctions(TestConfig("thresholdInClass" to "0"))

        val findings = rule.lint(resourceAsPath("SuppressedElementsByClassAnnotation.kt"))

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should suppress StringLiteralDuplication on class level`() {
        val path = resourceAsPath("SuppressStringLiteralDuplication.kt")

        assertThat(StringLiteralDuplication().lint(path)).isEmpty()
    }
}
