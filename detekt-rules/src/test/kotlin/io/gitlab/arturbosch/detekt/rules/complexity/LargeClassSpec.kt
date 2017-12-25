package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertEquals

/**
 * @author Artur Bosch
 */
class LargeClassSpec : SubjectSpek<LargeClass>({
	subject { LargeClass() }

	describe("nested classes are also considered") {
		it("should detect only the nested large class which exceeds threshold 70") {
			subject.lint(Case.NestedClasses.path())
			assertEquals(subject.findings.size, 1)
		}
	}
})
