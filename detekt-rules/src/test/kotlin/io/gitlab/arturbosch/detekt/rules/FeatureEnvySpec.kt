package io.gitlab.arturbosch.detekt.rules

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class FeatureEnvySpec {

	@Test
	fun findOne() {
		val findings = FeatureEnvy().lint(Case.FeatureEnvy.path())

		assertThat(findings.size, equalTo(1))
	}
}