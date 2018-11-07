package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.cli.createFinding
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Artur Bosch
 * @author schalkms
 */
class BaselineFacadeTest : Spek({

	val dir = Files.createTempDirectory("baseline_format")

	describe("writing a new baseline file") {

		describe("without a source set id") {

			it("creates a baseline file without smells") {
				val baselineFile = newBaselineFile()
				val baselineFacade = BaselineFacade(baselineFile.toPath())
				baselineFacade.create(emptyList())

				assertThat(baselineFile.readText()).isNotBlank()
			}
			it("creates a baseline file with smells") {
				val baselineFile = newBaselineFile()
				val ruleName = "TheRule"
				val baselineFacade = BaselineFacade(baselineFile.toPath())
				baselineFacade.create(
						listOf(createFinding(ruleName))
				)

				assertThat(baselineFile.readText()).contains(ruleName)
			}
		}

		describe("with a source set id") {
			val sourceSetId = "project:main"

			it("creates a baseline file without smells") {
				val baselineFile = newBaselineFile()
				val baselineFacade = BaselineFacade(baselineFile.toPath(), sourceSetId)
				baselineFacade.create(emptyList())

				assertThat(baselineFile.readText()).isNotBlank()
			}
			it("creates a baseline file with smells") {
				val baselineFile = newBaselineFile()
				val ruleName = "TheRule"
				val baselineFacade = BaselineFacade(baselineFile.toPath(), sourceSetId)
				baselineFacade.create(
						listOf(createFinding(ruleName))
				)

				assertThat(baselineFile.readText()).contains(ruleName)
			}
		}
	}

	describe("writing to an existing baseline file") {

		describe("without a source set id") {

			it("overwrites the default whitelist") {
				val baselineFile = newBaselineFile()
				val baselineFacade = BaselineFacade(baselineFile.toPath())
				val oldRule = "OLD_RULE"
				baselineFacade.create(listOf(createFinding(oldRule)))
				assertThat(baselineFile.readText()).contains(oldRule)

				// udpate with new whitelist
				val newRule = "NEW_RULE"
				baselineFacade.create(listOf(createFinding(newRule)))

				// verify
				assertThat(baselineFile.readText()).doesNotContain(oldRule)
				assertThat(baselineFile.readText()).contains(newRule)
			}
			it("adds a default whitelist") {
				val baselineFile = newBaselineFile()
				val withSourceSetFacade = BaselineFacade(baselineFile.toPath(), "project:foo")
				val noSourceSetFacade = BaselineFacade(baselineFile.toPath())
				val oldRule = "OLD_RULE"
				withSourceSetFacade.create(listOf(createFinding(oldRule)))
				assertThat(baselineFile.readText()).contains(oldRule)

				// udpate with new whitelist
				val newRule = "NEW_RULE"
				noSourceSetFacade.create(listOf(createFinding(newRule)))

				// verify
				assertThat(baselineFile.readText()).contains(oldRule)
				assertThat(baselineFile.readText()).contains(newRule)
			}
		}
		describe("with a source set id") {

			it("overwrites the project whitelist") {
				val baselineFile = newBaselineFile()
				val baselineFacade = BaselineFacade(baselineFile.toPath(), "project")
				val oldRule = "OLD_RULE"
				baselineFacade.create(listOf(createFinding(oldRule)))
				assertThat(baselineFile.readText()).contains(oldRule)

				// udpate with new whitelist
				val newRule = "NEW_RULE"
				baselineFacade.create(listOf(createFinding(newRule)))

				// verify
				assertThat(baselineFile.readText()).doesNotContain(oldRule)
				assertThat(baselineFile.readText()).contains(newRule)
			}
			it("adds to a default whitelist") {
				val baselineFile = newBaselineFile()
				val noSourceSetFacade = BaselineFacade(baselineFile.toPath())
				val withSourceSetFacade = BaselineFacade(baselineFile.toPath(), "project:foo")
				val oldRule = "OLD_RULE"
				noSourceSetFacade.create(listOf(createFinding(oldRule)))
				assertThat(baselineFile.readText()).contains(oldRule)

				// udpate with new whitelist
				val newRule = "NEW_RULE"
				withSourceSetFacade.create(listOf(createFinding(newRule)))

				// verify
				assertThat(baselineFile.readText()).contains(oldRule)
				assertThat(baselineFile.readText()).contains(newRule)
			}
			it("adds to a whitelist from a different project") {
				val baselineFile = newBaselineFile()
				val existingSourceSetFacade = BaselineFacade(baselineFile.toPath(), "project:foo")
				val newSourceSetFacade = BaselineFacade(baselineFile.toPath(), "project:bar")
				val oldRule = "OLD_RULE"
				existingSourceSetFacade.create(listOf(createFinding(oldRule)))
				assertThat(baselineFile.readText()).contains(oldRule)

				// udpate with new whitelist
				val newRule = "NEW_RULE"
				newSourceSetFacade.create(listOf(createFinding(newRule)))

				// verify
				assertThat(baselineFile.readText()).contains(oldRule)
				assertThat(baselineFile.readText()).contains(newRule)
			}
		}
	}

	describe("filtering smells") {
		it("filterWithExistingBaseline") {
			val findings = listOf(createFinding())
			val result = BaselineFacade(dir).filter(findings)
			assertThat(result).isEqualTo(findings)
		}

		it("filterWithoutExistingBaseline") {
			val path = Paths.get(resource("/smell-baseline.xml"))
			val findings = listOf<Finding>(createFinding())
			val result = BaselineFacade(path).filter(findings)
			assertThat(result).isEqualTo(findings)
		}
	}
})

private fun newBaselineFile(): File {
	val baselineFile = createTempDir("baseline-").resolve("baseline.xml")
	return baselineFile
}

