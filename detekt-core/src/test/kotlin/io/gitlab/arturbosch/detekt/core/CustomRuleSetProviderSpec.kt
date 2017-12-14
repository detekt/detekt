package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class CustomRuleSetProviderSpec : Spek({

	describe("custom rule sets should be loadable through jars") {

		val sampleRuleSet = Paths.get(resource("sample-rule-set.jar"))

		it("should load the sample provider") {
			val settings = ProcessingSettings(path, excludeDefaultRuleSets = true, pluginPaths = listOf(sampleRuleSet))
			val detekt = DetektFacade.create(settings)
			val result = detekt.run()

			assertThat(result.findings.keys).contains("sample")
		}
	}
})
