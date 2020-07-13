package io.gitlab.arturbosch.detekt.rules.exceptions

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ExceptionRaisedInUnexpectedLocationSpec : Spek({
    val subject by memoized { ExceptionRaisedInUnexpectedLocation() }

    describe("ExceptionRaisedInUnexpectedLocation rule") {

        it("reports methods raising an unexpected exception") {
            val path = resourceAsPath("ExceptionRaisedInMethodsPositive.kt")
            assertThat(subject.lint(path)).hasSize(5)
        }

        it("does not report methods raising no exception") {
            val path = resourceAsPath("ExceptionRaisedInMethodsNegative.kt")
            assertThat(subject.lint(path)).isEmpty()
        }

        it("reports the configured method") {
            val config = TestConfig(mapOf(ExceptionRaisedInUnexpectedLocation.METHOD_NAMES to listOf("toDo", "todo2")))
            val findings = ExceptionRaisedInUnexpectedLocation(config).compileAndLint("""
            fun toDo() {
                throw IllegalStateException()
            }""")
            assertThat(findings).hasSize(1)
        }

        it("reports the configured method with String") {
            val config = TestConfig(mapOf(ExceptionRaisedInUnexpectedLocation.METHOD_NAMES to "toDo,todo2"))
            val findings = ExceptionRaisedInUnexpectedLocation(config).compileAndLint("""
            fun toDo() {
                throw IllegalStateException()
            }""")
            assertThat(findings).hasSize(1)
        }
    }
})
