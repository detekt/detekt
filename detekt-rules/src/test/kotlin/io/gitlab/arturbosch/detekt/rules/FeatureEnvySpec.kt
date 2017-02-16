package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class FeatureEnvySpec {

	@Test
	fun findOne() {
		FeatureEnvy().lint(Case.FeatureEnvy.path())
	}
}