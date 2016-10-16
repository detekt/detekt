package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.Case
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.load
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
class CommonSpec : SubjectSpek<Rule>({
	subject { WildcardImport() }
	val root = load(Case.Default)

	describe("running specified rule") {
		it("should detect one finding") {
			subject.visit(root)
			assertTrue(subject.findings.size == 1)
		}
	}
})
