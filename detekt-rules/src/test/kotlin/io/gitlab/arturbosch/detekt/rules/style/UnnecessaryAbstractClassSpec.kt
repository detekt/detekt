package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class UnnecessaryAbstractClassSpec : SubjectSpek<UnnecessaryAbstractClass>({
	subject { UnnecessaryAbstractClass(Config.empty) }

	val noConcreteMemberDescription = "An abstract class without a concrete member can be refactored to an interface."
	val noAbstractMemberDescription = "An abstract class without an abstract member can be refactored to a concrete class."

	given("abstract classes with some members") {

		val file = compileForTest(Case.UnnecessaryAbstractClass.path())
		val findings = subject.lint(file.text)

		it("has no abstract member violation") {
			assertThat(countViolationsWithDescription(findings, noAbstractMemberDescription)).isEqualTo(3)
		}

		it("has no concrete member violation") {
			assertThat(countViolationsWithDescription(findings, noConcreteMemberDescription)).isEqualTo(1)
		}
	}
})

private fun countViolationsWithDescription(findings: List<Finding>, description: String) =
		findings.count { it.message.contains(description) }
