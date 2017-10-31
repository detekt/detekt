package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class TooManyFunctionsSpec : SubjectSpek<TooManyFunctions>({
	subject { TooManyFunctions() }

	describe("a simple test") {
		it("should find one file with too many functions") {
			assertThat(subject.lint(Case.TooManyFunctions.path())).hasSize(1)
		}

		it("should find one file with too many top level functions") {
			assertThat(subject.lint(Case.TooManyFunctionsTopLevel.path())).hasSize(1)
		}
	}

})
