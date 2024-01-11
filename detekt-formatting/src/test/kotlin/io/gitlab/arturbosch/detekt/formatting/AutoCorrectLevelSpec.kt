package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat as assertJThat

class AutoCorrectLevelSpec {

    @Test
    fun `autoCorrect_ true on all levels should reformat the test file`() {
        val config = yamlConfigFromContent(
            """
                formatting:
                  active: true
                  autoCorrect: true
                  ChainWrapping:
                    active: true
                    autoCorrect: true
            """.trimIndent()
        )

        val (file, findings) = runRule(config)

        assertThat(findings).isNotEmpty()
        assertJThat(wasFormatted(file)).isTrue()
    }

    @Test
    fun `autoCorrect_ false on ruleSet level should not reformat the test file`() {
        val config = yamlConfigFromContent(
            """
                formatting:
                  active: true
                  autoCorrect: false
                  ChainWrapping:
                    active: true
                    autoCorrect: true
            """.trimIndent()
        )

        val (file, findings) = runRule(config)

        assertThat(findings).isNotEmpty()
        assertJThat(wasFormatted(file)).isFalse()
    }

    @Test
    fun `autoCorrect_ false on rule level should not reformat the test file`() {
        val config = yamlConfigFromContent(
            """
                formatting:
                  active: true
                  autoCorrect: true
                  ChainWrapping:
                    active: true
                    autoCorrect: false
            """.trimIndent()
        )

        val (file, findings) = runRule(config)

        assertThat(findings).isNotEmpty()
        assertJThat(wasFormatted(file)).isFalse()
    }
}

private fun runRule(config: Config): Pair<KtFile, List<Finding>> {
    val testFile = loadFile("configTests/fixed.kt")
    val ruleSet = loadRuleSet<FormattingProvider>()
    val rules = ruleSet.rules.map { (ruleId, provider) -> provider(config.subConfig(ruleSet.id).subConfig(ruleId)) }
        .filter { it.config.valueOrDefault("active", false) }
    rules.forEach { it.visitFile(testFile) }
    return testFile to rules.flatMap { it.findings }
}

private fun wasFormatted(file: KtFile) = file.text == contentAfterChainWrapping

private inline fun <reified T : RuleSetProvider> loadRuleSet(): RuleSet {
    val provider = T::class.java.constructors[0].newInstance() as? T
        ?: error("Could not load RuleSet for '${T::class.java}'")
    return provider.instance()
}
