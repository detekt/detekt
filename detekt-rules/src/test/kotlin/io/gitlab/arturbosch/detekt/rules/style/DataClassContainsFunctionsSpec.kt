package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Ivan Balaksha
 */

class DataClassContainsFunctionsSpec : SubjectSpek<DataClassContainsFunctionsRule>({
	subject { DataClassContainsFunctionsRule() }

	given("several data classes") {

		it("valid data class") {
			assertThat(subject.lint(Case.DataClassContainsFunctions.path())).hasSize(2)
		}
	}
})
