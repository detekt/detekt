package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class LongMethodSpec : SubjectSpek<LongMethod>({

	subject { LongMethod(threshold = 3) }

	describe("nested functions can be long") {

		it("should find two long methods") {
			val path = Case.LongMethodPositive.path()
			assertThat(subject.lint(path)).hasSize(2)
		}

		it("should not find too long methods") {
			val path = Case.LongMethodNegative.path()
			assertThat(subject.lint(path)).isEmpty()
		}
	}
})
