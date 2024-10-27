package io.github.detekt.compiler.plugin.util

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.COMPILATION_ERROR
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.OK
import org.assertj.core.api.AbstractObjectAssert

fun assertThat(result: JvmCompilationResult) = CompilationAssert(result)

class CompilationAssert(private val result: JvmCompilationResult) :
    AbstractObjectAssert<CompilationAssert, JvmCompilationResult>(result, CompilationAssert::class.java) {

    private val detektMessages = result.messages.split("\n").dropWhile { !(it.contains(Regex("KClass\\d.kt"))) }

    private val regex = "\\w+\\.kt:\\d+:\\d+ (\\w+): .*".toRegex()
    private val detektViolations = detektMessages.mapNotNull { line -> regex.find(line)?.groupValues?.get(1) }

    fun passCompilation(expectedStatus: Boolean = true) = apply {
        val expectedErrorCode = if (expectedStatus) OK else COMPILATION_ERROR
        if (result.exitCode != expectedErrorCode) {
            failWithActualExpectedAndMessage(
                result.exitCode,
                expectedErrorCode,
                "Expected compilation to finish with code $expectedErrorCode but was ${actual.exitCode}.\n" +
                    actual.messages,
            )
        }
    }

    fun passDetekt(expectedStatus: Boolean = true) = apply {
        // The status message is `i: Success?: false`
        val status = detektMessages
            .first { "Success?" in it }
            .split(" ")
            .last()
            .trim()
            .toBoolean()

        if (status != expectedStatus) {
            failWithActualExpectedAndMessage(
                status,
                expectedStatus,
                "Expected detekt to finish with success status: $expectedStatus but was $status.\n" +
                    actual.messages,
            )
        }
    }

    fun withNoViolations() = withViolations(0)

    fun withViolations(expectedViolationNumber: Int) = apply {
        if (detektViolations.size != expectedViolationNumber) {
            failWithActualExpectedAndMessage(
                detektViolations.size,
                expectedViolationNumber,
                "Expected detekt violations to be $expectedViolationNumber but was ${detektViolations.size}.\n" +
                    actual.messages,
            )
        }
    }

    fun withRuleViolationInOrder(expectedRuleNames: List<String>) {
        if (detektViolations != expectedRuleNames) {
            failWithMessage(
                "Expected rules $expectedRuleNames to raise a violation but not all were found. " +
                    "Found violations are instead $detektViolations.\n" +
                    actual.messages,
            )
        }
    }
}
