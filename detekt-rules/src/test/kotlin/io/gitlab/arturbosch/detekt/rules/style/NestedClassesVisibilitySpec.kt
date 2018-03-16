package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class NestedClassesVisibilitySpec : SubjectSpek<NestedClassesVisibility>({
	subject { NestedClassesVisibility() }

	given("several nested classes") {

		it("reports public nested classes") {
			assertThat(subject.lint(Case.NestedClassVisibilityPositive.path())).hasSize(6)
		}

		it("does not report internal and (package) private nested classes") {
			assertThat(subject.lint(Case.NestedClassVisibilityNegative.path())).isEmpty()
		}
	}
})
