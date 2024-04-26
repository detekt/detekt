package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.compileForTest
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClass
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CorrectableRulesFirstSpec {

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `runs the correctable rules first, the registration order doesn't matter`(reverse: Boolean) {
        val testFile = path.resolve("Test.kt")
        val settings = createProcessingSettings(
            testFile,
            yamlConfigFromContent(
                """
                    Test:
                      NonCorrectable:
                        active: true
                        autoCorrect: false
                      Correctable:
                        active: true
                        autoCorrect: true
                """.trimIndent()
            ),
        ) { rules { autoCorrect = true } }
        val detector = Analyzer(
            settings,
            listOf(object : RuleSetProvider {
                override val ruleSetId = RuleSet.Id("Test")
                override fun instance() = RuleSet(
                    ruleSetId,
                    listOf(::NonCorrectable, ::Correctable).let { if (reverse) it.reversed() else it }
                )
            }),
            emptyList()
        )

        settings.use { detector.run(listOf(compileForTest(testFile))) }

        assertThat(actualLastRuleName).isEqualTo("NonCorrectable")
    }
}

private var actualLastRuleName = ""

private class NonCorrectable(config: Config) : Rule(config, "") {
    override fun visitClass(klass: KtClass) {
        actualLastRuleName = ruleName.value
    }
}

private class Correctable(config: Config) : Rule(config, "") {
    override fun visitClass(klass: KtClass) {
        actualLastRuleName = ruleName.value
    }
}
