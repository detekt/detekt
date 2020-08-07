package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.test.loadRuleSet
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class AutoCorrectLevelSpec : Spek({

    describe("test different autoCorrect levels in configuration") {

        describe("autoCorrect: true on all levels") {

            it("should reformat the test file") {
                val config = yamlConfig("/autocorrect/autocorrect-all-true.yml")

                val (file, findings) = runRule(config)

                assertThat(wasLinted(findings)).isTrue()
                assertThat(wasFormatted(file)).isTrue()
            }
        }

        describe("autoCorrect: false on ruleSet level") {

            it("should not reformat the test file") {
                val config = yamlConfig("/autocorrect/autocorrect-ruleset-false.yml")

                val (file, findings) = runRule(config)

                assertThat(wasLinted(findings)).isTrue()
                assertThat(wasFormatted(file)).isFalse()
            }
        }

        describe("autoCorrect: false on rule level") {

            it("should not reformat the test file") {
                val config = yamlConfig("/autocorrect/autocorrect-rule-false.yml")

                val (file, findings) = runRule(config)

                assertThat(wasLinted(findings)).isTrue()
                assertThat(wasFormatted(file)).isFalse()
            }
        }

        describe("autoCorrect: true but rule active false") {

            it("should not reformat the test file") {
                val config = yamlConfig("/autocorrect/autocorrect-true-rule-active-false.yml")

                val (file, findings) = runRule(config)

                assertThat(wasLinted(findings)).isFalse()
                assertThat(wasFormatted(file)).isFalse()
            }
        }
    }
})

private fun runRule(config: Config): Pair<KtFile, List<Finding>> {
    val testFile = loadFile("configTests/fixed.kt")
    val ruleSet = loadRuleSet<FormattingProvider>(config)
    ruleSet.rules.forEach { it.visitFile(testFile) }
    return testFile to ruleSet.rules.flatMap { it.findings }
}

private fun wasLinted(findings: List<Finding>) = findings.isNotEmpty()
private fun wasFormatted(file: KtFile) = file.text == contentAfterChainWrapping
