package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.documentation.NoDocOverPublicMethod
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class NoDocOverPublicMethodSpec : SubjectSpek<NoDocOverPublicMethod>({
	subject { NoDocOverPublicMethod() }

	it("finds three undocumented functions") {
		val root = load(Case.Comments)
		subject.visit(root)
		assertThat(subject.findings).hasSize(3)
	}
})