package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ThrowingExceptionInMainSpec : Spek({
    val subject by memoized { ThrowingExceptionInMain() }

    describe("ThrowingExceptionInMain rule") {

        it("has a runnable main method which throws an exception") {
            val code = "fun main(args: Array<String>) { throw IllegalArgumentException() }"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("has wrong main methods") {
            val file = compileContentForTest(
                """
                    fun main(args: Array<String>) { }
                    private fun main() { }
                    fun mai() { }
                    fun main(args: String) { }"""
            )
            assertThat(subject.lint(file)).hasSize(0)
        }
    }
})
