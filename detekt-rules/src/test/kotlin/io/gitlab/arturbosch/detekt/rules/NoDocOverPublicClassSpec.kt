package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.documentation.NoDocOverPublicClass
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class NoDocOverPublicClassSpec : SubjectSpek<NoDocOverPublicClass>({
	subject { NoDocOverPublicClass() }

	it("finds two undocumented classes") {
		subject.lint(Case.Comments.path())
		assertThat(subject.findings).hasSize(2)
	}

})