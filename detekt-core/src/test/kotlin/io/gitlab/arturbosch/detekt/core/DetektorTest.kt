package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
class DetektorTest {

	@Test
	fun `TestProvider gets excluded as RuleSet`() {
		runDetektWithPattern("patterns/test-pattern.yml")
	}

	@Test
	fun `FindName rule gets excluded`() {
		runDetektWithPattern("patterns/exclude-FindName.yml")
	}

	private fun runDetektWithPattern(patternToUse: String) {
		val instance = DetektFacade.create(ProcessingSettings(path,
				config = yamlConfig(patternToUse)),
				listOf(TestProvider(), TestProvider2()), emptyList())

		val run = instance.run()
		assertTrue { run.findings["Test"]?.none { "Test.kt" in it.file } ?: true }
	}
}
