package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
class NamingConventionViolationSpec : SubjectSpek<NamingConventionViolation>({
	subject { NamingConventionViolation() }

	it("should find all wrong namings") {
		val root = load(Case.NamingConventions)
		subject.visit(root)
		assertThat(subject.findings).hasSize(9)
	}

})

class NamingConventionTest {

	@Test
	fun lint() {
		assertThat(NamingConventionViolation().lint(
				"""
            const val MY_NAME = "Artur"
            const val MYNAME = "Artur"
            const val MyNAME = "Artur"
            const val serialVersionUID = 42L
            """
		)).hasSize(1)
	}

	@Test
	fun uppercaseAllowedForVariablesInsideObjectDeclaration() {
		assertThat(NamingConventionViolation().lint(
				"""
			object Bla {
				val MY_NAME = "Artur"
			}
            """
		)).hasSize(0)
	}
}