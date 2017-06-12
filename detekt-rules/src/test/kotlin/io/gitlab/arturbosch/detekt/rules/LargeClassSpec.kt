package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.complexity.LargeClass
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

/**
 * @author Artur Bosch
 */
class LargeClassSpec : SubjectSpek<LargeClass>({
	subject { LargeClass() }
	itBehavesLike(CommonSpec())

	describe("nested classes are also considered") {
		it("should detect only the nested large class") {
            val findings = subject.lint(Case.NestedClasses.path())
            assertThat(findings).hasSize(1)
		}
	}
})