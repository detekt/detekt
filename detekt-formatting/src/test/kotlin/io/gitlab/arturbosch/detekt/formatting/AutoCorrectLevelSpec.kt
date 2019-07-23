package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.test.loadRuleSet
import io.gitlab.arturbosch.detekt.test.resource
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

// Error:(31, 49) Kotlin: Cannot infer type parameter SELF in
// val <SELF : AbstractBooleanAssert<SELF!>!> AbstractBooleanAssert<SELF>.isTrue: SELF!
@Suppress("UsePropertyAccessSyntax")
class AutoCorrectLevelSpec : Spek({

    describe("test different autoCorrect levels in configuration") {

        describe("autoCorrect: true on all levels") {

            val config = yamlConfig("/autocorrect/autocorrect-all-true.yml")

            it("should reformat the test file") {
                val (file, findings) = runAnalysis(config)
                assertThat(wasLinted(findings)).isTrue()
                assertThat(wasFormatted(file)).isTrue()
            }
        }

        describe("autoCorrect: false on top level") {

            val config = yamlConfig("/autocorrect/autocorrect-toplevel-false.yml")

            it("should format the test file but not print to disc") {
                val project = Paths.get(resource("configTests/fixed.kt"))
                var expectedContentBeforeRun: String? = null
                val contentChanged = object : FileProcessListener {
                    override fun onStart(files: List<KtFile>) {
                        assertThat(files).hasSize(1)
                        expectedContentBeforeRun = files[0].text
                    }

                    override fun onFinish(files: List<KtFile>, result: Detektion) {
                        assertThat(files).hasSize(1)
                        assertThat(wasFormatted(files[0])).isTrue()
                    }
                }
                val detekt = DetektFacade.create(
                    ProcessingSettings(project, config), listOf(FormattingProvider()), listOf(contentChanged))
                val findings = detekt.run().findings.flatMap { it.value }
                val actualContentAfterRun = loadFileContent("configTests/fixed.kt")

                assertThat(wasLinted(findings)).isTrue()
                assertThat(actualContentAfterRun).isEqualTo(expectedContentBeforeRun)
            }
        }

        describe("autoCorrect: false on ruleSet level") {

            val config = yamlConfig("/autocorrect/autocorrect-ruleset-false.yml")

            it("should not reformat the test file") {
                val (file, findings) = runAnalysis(config)
                assertThat(wasLinted(findings)).isTrue()
                assertThat(wasFormatted(file)).isFalse()
            }
        }

        describe("autoCorrect: false on rule level") {

            val config = yamlConfig("/autocorrect/autocorrect-rule-false.yml")

            it("should not reformat the test file") {
                val (file, findings) = runAnalysis(config)
                assertThat(wasLinted(findings)).isTrue()
                assertThat(wasFormatted(file)).isFalse()
            }
        }

        describe("autoCorrect: true but rule active false") {

            val config = yamlConfig("/autocorrect/autocorrect-true-rule-active-false.yml")

            it("should not reformat the test file") {
                val (file, findings) = runAnalysis(config)
                assertThat(wasLinted(findings)).isFalse()
                assertThat(wasFormatted(file)).isFalse()
            }
        }
    }
})

private fun runAnalysis(config: Config): Pair<KtFile, List<Finding>> {
    val testFile = loadFile("configTests/fixed.kt")
    val ruleSet = loadRuleSet<FormattingProvider>(config)
    val findings = ruleSet.accept(testFile)
    return testFile to findings
}

private fun wasLinted(findings: List<Finding>): Boolean = findings.isNotEmpty()
private fun wasFormatted(file: KtFile): Boolean = file.text == contentAfterChainWrapping
