package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class EmptyFunctionBlockSpec : SubjectSpek<EmptyFunctionBlock>({

	subject { EmptyFunctionBlock(Config.empty) }

	describe("should not flag functions meant to be overridden") {

		it("should flag function with protected modifier") {
			val findings = subject.lint("protected fun stuff() {}")
			assertThat(findings).hasSize(1)
		}

		it("should not flag function with open modifier") {
			val findings = subject.lint("open fun stuff() {}")
			assertThat(findings).isEmpty()
		}
	}

})
