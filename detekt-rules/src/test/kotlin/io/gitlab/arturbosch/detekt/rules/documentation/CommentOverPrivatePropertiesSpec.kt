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
class CommentOverPrivatePropertiesSpec : SubjectSpek<CommentOverPrivateProperty>({
	subject { CommentOverPrivateProperty() }

	given("some properties with comments") {

		it("reports private property with a comment") {
			val code = """
				/**
				 * asdf
				 */
				private val v = 1"""
			assertThat(subject.compileAndLint(code)).hasSize(1)
		}

		it("does not report public property with a comment") {
			val code = """
				/**
				 * asdf
				 */
				val v = 1"""
			assertThat(subject.compileAndLint(code)).hasSize(0)
		}

		it("reports private property in class with a comment") {
			val code = """
					class Test {
					/**
					 * asdf
					 */
					private val v = 1
				}"""
			assertThat(subject.compileAndLint(code)).hasSize(1)
		}

		it("does not report public property with a comment") {
			val code = """
				class Test {
					/**
					 * asdf
					 */
					val v = 1
				}"""
			assertThat(subject.compileAndLint(code)).hasSize(0)
		}
	}
})
