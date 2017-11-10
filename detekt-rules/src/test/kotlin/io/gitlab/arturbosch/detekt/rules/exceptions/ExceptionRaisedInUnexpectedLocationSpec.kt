package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ExceptionRaisedInUnexpectedLocationSpec : SubjectSpek<ExceptionRaisedInUnexpectedLocation>({
	subject {
		ExceptionRaisedInUnexpectedLocation(
				TestConfig(mapOf(ExceptionRaisedInUnexpectedLocation.METHOD_NAMES to "toString,hashCode,equals,finalize"))
		)
	}

	given("methods which are not expected to throw exceptions") {

		it("reports the methods raising an unexpected exception") {
			val file = compileForTest(Case.ExceptionRaisedInMethods.path())
			val findings = subject.lint(file.text)
			assertThat(findings).hasSize(5)
		}
	}

	given("a configuration with a custom method") {

		it("reports the configured method") {
			val config = TestConfig(mapOf(ExceptionRaisedInUnexpectedLocation.METHOD_NAMES to "toDo,todo2"))
			val findings = ExceptionRaisedInUnexpectedLocation(config).lint("""
				fun toDo() {
					throw IllegalStateException()
				}""")
			assertThat(findings).hasSize(1)
		}
	}
})

