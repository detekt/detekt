package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
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
			assertThat(subject.lint(code)).hasSize(1)
		}

		it("does not report public property with a comment") {
			val code = """
				/**
				 * asdf
				 */
				val v = 1"""
			assertThat(subject.lint(code)).hasSize(0)
		}
	}
})
