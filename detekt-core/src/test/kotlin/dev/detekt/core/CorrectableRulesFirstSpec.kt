package dev.detekt.core

import dev.detekt.api.Config
import dev.detekt.api.Rule
import dev.detekt.test.compileForTest
import dev.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClass
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CorrectableRulesFirstSpec {

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `runs the correctable rules first, the registration order doesn't matter`(reverse: Boolean) {
        val testFile = path.resolve("Test.kt")
        val config = yamlConfigFromContent(
            """
                Test:
                  NonCorrectable:
                    active: true
                    autoCorrect: false
                  Correctable:
                    active: true
                    autoCorrect: true
            """.trimIndent()
        )
        val settings = createProcessingSettings { rules { autoCorrect = true } }
        val detector = Analyzer(
            settings,
            *listOf(
                createRuleDescriptor(::NonCorrectable, config.subConfig("Test").subConfig("NonCorrectable")),
                createRuleDescriptor(::Correctable, config.subConfig("Test").subConfig("Correctable")),
            )
                .let { if (reverse) it.reversed() else it }
                .toTypedArray(),
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
