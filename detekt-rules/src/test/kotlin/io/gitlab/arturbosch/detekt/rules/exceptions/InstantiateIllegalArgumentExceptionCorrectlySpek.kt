package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class InstantiateIllegalArgumentExceptionCorrectlySpek : SubjectSpek<InstantiateIllegalArgumentExceptionCorrectly>({
	subject { InstantiateIllegalArgumentExceptionCorrectly() }

	given("several IllegalArgumentException calls") {

		it("reports calls to the default constructor") {
			val code = """
				fun x() {
					IllegalArgumentException(IllegalArgumentException())
					IllegalArgumentException("foo")
					throw IllegalArgumentException()
				}"""
			assertThat(subject.lint(code)).hasSize(2)
		}
	}
})
