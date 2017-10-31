package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ProtectedMemberInFinalClassSpec : SubjectSpek<ProtectedMemberInFinalClass>({
	subject { ProtectedMemberInFinalClass(Config.empty) }
	val file = compileForTest(Case.FinalClass.path())

	describe("check all variants of protected visibility modifier in final class") {

		it("has protected visibility") {
			val findings = subject.lint(file.text)
			Assertions.assertThat(findings).hasSize(13)
		}
	}
})
