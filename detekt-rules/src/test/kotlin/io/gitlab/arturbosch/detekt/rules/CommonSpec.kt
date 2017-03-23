package io.gitlab.arturbosch.detekt.rules

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.style.WildcardImport
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
			assertThat(subject.findings, hasSize(equalTo(1)))
		}
	}
})
