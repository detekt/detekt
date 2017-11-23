package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class NamingConventionViolationSpec : SubjectSpek<NamingRules>({

	subject { NamingRules() }

	it("should find all wrong namings") {
		subject.lint(Case.NamingConventions.path())
		assertThat(subject.findings).hasSize(13)
	}
})
