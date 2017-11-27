package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class LongParameterListSpec : SubjectSpek<LongParameterList>({
	subject { LongParameterList() }

	given("function with parameters") {

		it("reports too long parameter list") {
			val code = "fun long(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) {}"
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("does not reports short parameter list") {
			val code = "fun long(a: Int, b: Int, c: Int, d: Int, e: Int) {}"
			assertThat(subject.lint(code)).isEmpty()
		}
	}
})
