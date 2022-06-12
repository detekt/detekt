package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.test.loadRuleSet
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test

class AutoCorrectLevelSpec {

    @Test
    fun `autoCorrect_ true on all levels should reformat the test file`() {
        val config = yamlConfig("/autocorrect/autocorrect-all-true.yml")

        val (file, findings) = runRule(config)

        assertThat(wasLinted(findings)).isTrue()
        assertThat(wasFormatted(file)).isTrue()
    }

    @Test
    fun `autoCorrect_ false on ruleSet level should not reformat the test file`() {
        val config = yamlConfig("/autocorrect/autocorrect-ruleset-false.yml")

        val (file, findings) = runRule(config)

        assertThat(wasLinted(findings)).isTrue()
        assertThat(wasFormatted(file)).isFalse()
    }

    @Test
    fun `autoCorrect_ false on rule level should not reformat the test file`() {
        val config = yamlConfig("/autocorrect/autocorrect-rule-false.yml")

        val (file, findings) = runRule(config)

        assertThat(wasLinted(findings)).isTrue()
        assertThat(wasFormatted(file)).isFalse()
    }

    @Test
    fun `autoCorrect_ true but rule active false should not reformat the test file`() {
        val config = yamlConfig("/autocorrect/autocorrect-true-rule-active-false.yml")

        val (file, findings) = runRule(config)

        assertThat(wasLinted(findings)).isFalse()
        assertThat(wasFormatted(file)).isFalse()
    }
}

private fun runRule(config: Config): Pair<KtFile, List<Finding>> {
    val testFile = loadFile("configTests/fixed.kt")
    val ruleSet = loadRuleSet<FormattingProvider>(config)
    ruleSet.rules.forEach { it.visitFile(testFile) }
    return testFile to ruleSet.rules.flatMap { it.findings }
}

private fun wasLinted(findings: List<Finding>) = findings.isNotEmpty()
private fun wasFormatted(file: KtFile) = file.text == contentAfterChainWrapping
