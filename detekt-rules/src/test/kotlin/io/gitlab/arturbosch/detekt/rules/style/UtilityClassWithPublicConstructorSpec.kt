package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class UtilityClassWithPublicConstructorSpec : SubjectSpek<UtilityClassWithPublicConstructor>({
	subject { UtilityClassWithPublicConstructor(Config.empty) }

	given("several utility classes") {

		it("reports utility classes with a public constructor") {
			val findings = subject.lint(Case.UtilityClassesPositive.path())
			assertThat(findings).hasSize(4)
		}

		it("does not report utility classes with no public constructor") {
			val findings = subject.lint(Case.UtilityClassesNegative.path())
			assertThat(findings).hasSize(0)
		}
	}
})
