package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.loadRuleSet
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class KtLintIntegrationSpec : Spek({

	describe("tests integration of formatting") {

		it("should work like KtLint") {
			val fileBefore = loadFile("before.kt")
			val expected = loadFileContent("after.kt")

			val ruleSet = loadRuleSet<FormattingProvider>(
					TestConfig(mapOf("autoCorrect" to "true")))
			val findings = ruleSet.accept(fileBefore)

			assertThat(findings).isNotEmpty
			assertThat(fileBefore.text).isEqualTo(expected)
		}
	}
})
