package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.style.WildcardImport
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class CommonSpec : SubjectSpek<Rule>({
	subject { WildcardImport() }
	val file = compileForTest(Case.Default.path())

	describe("running specified rule") {
		it("should detect one finding") {
			subject.lint(file.text)
			assertThat(subject.findings).hasSize(1)
		}
	}
})