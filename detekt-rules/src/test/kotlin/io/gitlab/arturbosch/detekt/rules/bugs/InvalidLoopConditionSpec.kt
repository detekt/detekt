package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class InvalidLoopConditionSpec : SubjectSpek<InvalidLoopCondition>({
	subject { InvalidLoopCondition(Config.empty) }

	describe("check loop conditions") {

		it("reports invalid loop conditions") {
			val file = compileForTest(Case.InvalidLoopCondition.path())
			assertThat(subject.lint(file.text)).hasSize(3)
		}
	}
})
