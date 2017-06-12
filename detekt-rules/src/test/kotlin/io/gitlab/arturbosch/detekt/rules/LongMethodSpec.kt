package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.complexity.LongMethod
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.assertj.core.api.Assertions.assertThat

/**
 * @author Artur Bosch
 */
class LongMethodSpec : SubjectSpek<LongMethod>({
	subject { LongMethod(threshold = 10) }

	describe("nested functions can be long") {
		it("should find two long methods") {
            val findings = subject.lint(Case.NestedLongMethods.path())
            assertThat(findings).hasSize(2)
		}
	}

	describe("nested classes can contain long methods") {
		it("should detect one nested long method") {
            val findings = subject.lint(Case.NestedClasses.path())
            assertThat(findings).hasSize(1)
		}

	}
})