package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Rule
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class CommonSpec : SubjectSpek<Rule>({
	subject { WildcardImport() }
	val root = load(Case.Default)

	describe("running specified rule") {
		it("should detect one finding") {
			subject.visit(root)
			assertThat(subject.findings).hasSize(1)
		}
	}
})
