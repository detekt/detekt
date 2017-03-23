package io.gitlab.arturbosch.detekt.rules

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class NoDocOverPublicClassSpec : SubjectSpek<NoDocOverPublicClass>({
	subject { NoDocOverPublicClass() }

	it("finds two undocumented classes") {
		val root = load(Case.Comments)
		subject.visit(root)
		assertThat(subject.findings).hasSize(2)
	}

})