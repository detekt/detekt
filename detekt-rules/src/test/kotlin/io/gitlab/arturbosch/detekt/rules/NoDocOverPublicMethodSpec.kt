package io.gitlab.arturbosch.detekt.rules

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it

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