package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class EmptyFunctionBlockSpec : SubjectSpek<EmptyFunctionBlock>({

	subject { EmptyFunctionBlock(Config.empty) }

	given("some functions") {

		it("should flag function with protected modifier") {
			val code = "protected fun stuff() {}"
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should not flag function with open modifier") {
			val code = "open fun stuff() {}"
			assertThat(subject.lint(code)).isEmpty()
		}
	}

	given("some overridden functions") {

		val code = """
				override fun stuff1() {}

				override fun stuff2() {
					TODO("Implement this")
				}

				override fun stuff3() {
					// this is necessary...
				}"""

		it("should flag empty block in overridden function") {
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("should not flag overridden functions") {
			val config = TestConfig(mapOf(EmptyFunctionBlock.IGNORE_OVERRIDDEN_FUNCTIONS to "true"))
			assertThat(EmptyFunctionBlock(config).lint(code)).isEmpty()
		}
	}

})
