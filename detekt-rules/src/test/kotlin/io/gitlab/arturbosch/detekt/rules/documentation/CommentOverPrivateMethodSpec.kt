package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 * @author schalkms
 */
class CommentOverPrivateMethodSpec : SubjectSpek<CommentOverPrivateFunction>({
	subject { CommentOverPrivateFunction() }

	given("some methods with comments") {

		it("reports private method with a comment") {
			val code = """
    			class Test {
					/**
					 * asdf
					 */
					private fun f() {}
				}"""
			assertThat(subject.compileAndLint(code)).hasSize(1)
		}

		it("does not report public method with a comment") {
			val code = """
				/**
				 * asdf
				 */
				fun f() {}"""
			assertThat(subject.compileAndLint(code)).hasSize(0)
		}

		it("does not report public method in a class with a comment") {
			val code = """
    			class Test {
					/**
					 * asdf
					 */
					fun f() {}
				}"""
			assertThat(subject.compileAndLint(code)).hasSize(0)
		}
	}
})
