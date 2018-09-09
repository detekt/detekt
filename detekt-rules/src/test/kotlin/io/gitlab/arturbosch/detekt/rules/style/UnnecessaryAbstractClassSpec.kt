package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class UnnecessaryAbstractClassSpec : SubjectSpek<UnnecessaryAbstractClass>({
	subject {
		UnnecessaryAbstractClass(TestConfig(mapOf(
				UnnecessaryAbstractClass.EXCLUDE_ANNOTATED_CLASSES to "jdk.nashorn.internal.ir.annotations.Ignore"
		)))
	}

	val noConcreteMemberDescription = "An abstract class without a concrete member can be refactored to an interface."
	val noAbstractMemberDescription = "An abstract class without an abstract member can be refactored to a concrete class."

	given("abstract classes with no abstract members") {

		val path = Case.UnnecessaryAbstractClassPositive.path()
		val findings = subject.lint(path)

		it("has no abstract member violation") {
			assertThat(countViolationsWithDescription(findings, noAbstractMemberDescription)).isEqualTo(5)
		}

		it("has no concrete member violation") {
			assertThat(countViolationsWithDescription(findings, noConcreteMemberDescription)).isEqualTo(1)
		}
	}

	given("abstract classes with members") {

		val path = Case.UnnecessaryAbstractClassNegative.path()
		val findings = subject.lint(path)

		it("does not report no abstract member violation") {
			assertThat(countViolationsWithDescription(findings, noAbstractMemberDescription)).isEqualTo(0)
		}

		it("does not report no concrete member violation") {
			assertThat(countViolationsWithDescription(findings, noConcreteMemberDescription)).isEqualTo(0)
		}
	}
})

private fun countViolationsWithDescription(findings: List<Finding>, description: String) =
		findings.count { it.message.contains(description) }
