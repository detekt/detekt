package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.compileForTest
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Issue
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
        var actualLastRuleId = ""

        class NonCorrectable(config: Config) : Rule(config) {
            override val issue: Issue = Issue(javaClass.simpleName, "")
            override fun visitClass(klass: KtClass) {
                actualLastRuleId = issue.id
            }
        }

        class Correctable(config: Config) : Rule(config) {
            override val issue: Issue = Issue(javaClass.simpleName, "")
            override fun visitClass(klass: KtClass) {
                actualLastRuleId = issue.id
            }
        }

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
                override val ruleSetId: String = "Test"
                override fun instance(config: Config) = RuleSet(
                    ruleSetId,
                    listOf(NonCorrectable(config), Correctable(config)).let { if (reverse) it.reversed() else it }
                )
            }),
            emptyList()
        )

        settings.use { detector.run(listOf(compileForTest(testFile))) }

        assertThat(actualLastRuleId).isEqualTo("NonCorrectable")
    }
}
