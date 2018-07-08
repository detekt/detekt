package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.test.loadRuleSet
import io.gitlab.arturbosch.detekt.test.resource
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import java.nio.file.Paths
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
class AutoCorrectLevelSpec : Spek({

	describe("test different autoCorrect levels in configuration") {

		given("autoCorrect: true on all levels") {

			val config = yamlConfig("/autocorrect/autocorrect-all-true.yml")

			it("should reformat the test file") {
				val (file, findings) = runAnalysis(config)
				assertTrue(wasLinted(findings))
				assertTrue(wasFormatted(file))
			}
		}

		given("autoCorrect: false on top level") {

			val config = yamlConfig("/autocorrect/autocorrect-toplevel-false.yml")

			it("should format the test file but not print to disc") {
				val project = Paths.get(resource("before.kt"))
				val detekt = DetektFacade.create(ProcessingSettings(project, config))
				val file = loadFile("before.kt")
				val expected = file.text
				val findings = detekt.run(project, listOf(file))
						.findings.flatMap { it.value }

				assertTrue(wasLinted(findings))
				assertTrue(wasFormatted(file))
				assertThat(loadFileContent("before.kt")).isEqualTo(expected)
			}
		}

		given("autoCorrect: false on ruleSet level") {

			val config = yamlConfig("/autocorrect/autocorrect-ruleset-false.yml")

			it("should not reformat the test file") {
				val (file, findings) = runAnalysis(config)
				assertTrue(wasLinted(findings))
				assertFalse(wasFormatted(file))
			}
		}

		given("autoCorrect: false on rule level") {

			val config = yamlConfig("/autocorrect/autocorrect-rule-false.yml")

			it("should not reformat the test file") {
				val (file, findings) = runAnalysis(config)
				assertTrue(wasLinted(findings))
				assertFalse(wasFormatted(file))
			}
		}

		given("autoCorrect: true but rule active false") {

			val config = yamlConfig("/autocorrect/autocorrect-true-rule-active-false.yml")

			it("should not reformat the test file") {
				val (file, findings) = runAnalysis(config)
				assertFalse(wasLinted(findings))
				assertFalse(wasFormatted(file))
			}
		}
	}
})

private fun runAnalysis(config: Config): Pair<KtFile, List<Finding>> {
	val testFile = loadFile("before.kt")
	val ruleSet = loadRuleSet<FormattingProvider>(config)
	val findings = ruleSet.accept(testFile)
	return testFile to findings
}

private fun wasLinted(findings: List<Finding>): Boolean = findings.isNotEmpty()
private fun wasFormatted(file: KtFile): Boolean = file.text == contentAfterChainWrapping
