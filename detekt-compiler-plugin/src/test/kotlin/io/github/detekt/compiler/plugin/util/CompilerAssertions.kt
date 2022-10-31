package io.github.detekt.compiler.plugin.util

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.*
import org.assertj.core.api.AbstractObjectAssert

fun assertThat(result: KotlinCompilation.Result) = CompilationAssert(result)

class CompilationAssert(private val result: KotlinCompilation.Result) :
    AbstractObjectAssert<CompilationAssert, KotlinCompilation.Result>(result, CompilationAssert::class.java) {

    private val detektMessages = result.messages.split("\n")
        .dropWhile { "Running detekt" !in it }
        .dropLastWhile { "Success?" !in it }

    private val regex = "\\w+\\.kt:\\d+:\\d+: .* \\[(\\w+)\\]".toRegex()
    private val detektViolations = detektMessages
        .mapNotNull { line -> regex.find(line)?.groupValues?.get(1) }

    fun passCompilation(expectedStatus : Boolean = true) = apply {
        val expectedErrorCode = if (expectedStatus) OK else COMPILATION_ERROR
        if (result.exitCode != expectedErrorCode) {
            failWithActualExpectedAndMessage(result.exitCode, expectedErrorCode,
                "Expected compilation to finish with " +
                    "code $expectedErrorCode but was ${result.exitCode}")
        }
    }

    fun passDetekt(expectedStatus : Boolean = true) = apply {
        // The status message is `i: Success?: false`
        val status = detektMessages
            .first { "Success?" in it }
            .split(" ")
            .last()
            .trim()
            .toBoolean()

        if (status != expectedStatus) {
            failWithActualExpectedAndMessage(status, expectedStatus,
                "Expected detekt to finish with " +
                    "success status: $expectedStatus but was $status")
        }
    }

    fun withNoViolations() = withViolations(0)

    fun withViolations(expectedViolationNumber: Int) = apply {
        if (detektViolations.size != expectedViolationNumber) {
            failWithActualExpectedAndMessage(detektViolations.size, expectedViolationNumber,
                "Expected detekt violations to be " +
                    "$expectedViolationNumber but was ${detektViolations.size}")
        }
    }

    fun withRuleViolation(vararg expectedRuleName: String) = apply {
        if (expectedRuleName.any { it !in detektViolations }) {
            failWithMessage("Expected rules ${expectedRuleName.toList()} to raise a violation " +
                    "but not all were found. Found violations are instead $detektViolations")
        }
    }
}
