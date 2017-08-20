package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class FeatureEnvySpec : SubjectSpek<FeatureEnvy>({
	subject { FeatureEnvy() }

	describe("running specified rule") {
		it("should detect one finding") {
			val findings = subject.lint(Case.FeatureEnvy.path())
			assertThat(findings.size).isEqualTo(1)
		}
	}
})
