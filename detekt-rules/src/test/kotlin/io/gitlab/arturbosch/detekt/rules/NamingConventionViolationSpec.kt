package io.gitlab.arturbosch.detekt.rules

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class NamingConventionViolationSpec : SubjectSpek<NamingConventionViolation>({
	subject { NamingConventionViolation() }

	it("should find all wrong namings") {
		val root = load(Case.NamingConventions)
		subject.visit(root)
		assertThat(subject.findings, hasSize(equalTo(9)))
	}

})