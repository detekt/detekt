package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.documentation.UndocumentedPublicFunction
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class UndocumentedPublicFunctionSpec : SubjectSpek<UndocumentedPublicFunction>({
	subject { UndocumentedPublicFunction() }

	it("finds three undocumented functions") {
		subject.lint(Case.Comments.path())
		assertThat(subject.findings).hasSize(3)
	}
})
