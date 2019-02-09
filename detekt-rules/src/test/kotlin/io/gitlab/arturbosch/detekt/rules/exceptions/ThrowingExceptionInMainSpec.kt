package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ThrowingExceptionInMainSpec : SubjectSpek<ThrowingExceptionInMain>({
    subject { ThrowingExceptionInMain() }

    given("some main methods") {

        it("has a runnable main method which throws an exception") {
            val code = "fun main(args: Array<String>) { throw new IOException() }"
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("has wrong main methods") {
            val code = """
				fun main(args: Array<String>) { }
				private fun main() { }
				fun mai() { }
				fun main(args: String) { }"""
            assertThat(subject.lint(code)).hasSize(0)
        }
    }
})
