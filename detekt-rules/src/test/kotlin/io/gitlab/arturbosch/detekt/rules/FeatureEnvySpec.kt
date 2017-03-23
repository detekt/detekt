package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class FeatureEnvySpec {

	@Test
	fun findOne() {
		val findings = FeatureEnvy().lint(Case.FeatureEnvy.path())

		assertThat(findings.size).isEqualTo(1)
	}
}