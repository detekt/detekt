package io.gitlab.arturbosch.detekt.rules

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
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
		assertThat(subject.findings, hasSize(equalTo(2)))
	}

})