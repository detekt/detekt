package dev.detekt.test

import dev.detekt.api.Finding
import dev.detekt.api.SourceLocation
import dev.detekt.api.TextLocation
import org.assertj.core.annotation.CheckReturnValue
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.AbstractListAssert
import java.util.Objects

@CheckReturnValue
fun assertThat(findings: List<Finding>) = FindingsAssert(findings)

@CheckReturnValue
fun assertThat(finding: Finding?) = FindingAssert(finding)

class FindingsAssert(actual: List<Finding>) :
    AbstractListAssert<FindingsAssert, List<Finding>, Finding, FindingAssert>(actual, FindingsAssert::class.java) {

    override fun newAbstractIterableAssert(iterable: MutableIterable<Finding>): FindingsAssert =
        throw UnsupportedOperationException("not implemented")

    override fun toAssert(value: Finding?, description: String?): FindingAssert =
        FindingAssert(value).`as`(description)

    fun hasStartSourceLocations(vararg expected: SourceLocation) = apply {
        val actualSources = actual.asSequence()
            .map { it.location.source }
            .sortedWith(compareBy({ it.line }, { it.column }))

        val expectedSources = expected.asSequence()
            .sortedWith(compareBy({ it.line }, { it.column }))

        if (!Objects.deepEquals(actualSources.toList(), expectedSources.toList())) {
            failWithMessage(
                "Expected start source locations to be ${expectedSources.toList()} but was ${actualSources.toList()}"
            )
        }
    }

    fun hasEndSourceLocations(vararg expected: SourceLocation) = apply {
        val actualSources = actual.asSequence()
            .map { it.location.endSource }
            .sortedWith(compareBy({ it.line }, { it.column }))

        val expectedSources = expected.asSequence()
            .sortedWith(compareBy({ it.line }, { it.column }))

        if (!Objects.deepEquals(actualSources.toList(), expectedSources.toList())) {
            failWithMessage(
                "Expected end source locations to be ${expectedSources.toList()} but was ${actualSources.toList()}"
            )
        }
    }

    fun hasTextLocations(vararg expected: Pair<Int, Int>) = apply {
        val actualSources = actual.asSequence()
            .map { it.location.text }
            .sortedWith(compareBy({ it.start }, { it.end }))

        val expectedSources = expected.asSequence()
            .map { (start, end) -> TextLocation(start, end) }
            .sortedWith(compareBy({ it.start }, { it.end }))

        if (!Objects.deepEquals(actualSources.toList(), expectedSources.toList())) {
            failWithMessage(
                "Expected text locations to be ${expectedSources.toList()} but was ${actualSources.toList()}"
            )
        }
    }

    fun hasTextLocations(vararg expected: String): FindingsAssert {
        val finding = actual.firstOrNull()
        if (finding == null) {
            if (expected.isEmpty()) {
                return this
            } else {
                failWithMessage("Expected ${expected.size} findings but was 0")
                // This should never execute. `failWithMessage` always throws an exception but the kotlin compiled
                // doesn't know that. So this line below helps it.
                error("This should never execute, if you find this please open an issue with a reproducer")
            }
        }
        val code = finding.entity.ktElement.containingKtFile.text

        val textLocations = expected.map { snippet ->
            val index = code.indexOf(snippet)
            if (index < 0) {
                failWithMessage("The snippet \"$snippet\" doesn't exist in the code")
            } else {
                if (code.indexOf(snippet, index + 1) >= 0) {
                    failWithMessage("The snippet \"$snippet\" appears multiple times in the code")
                }
            }
            index to index + snippet.length
        }.toTypedArray()

        return hasTextLocations(*textLocations)
    }
}

class FindingAssert(val actual: Finding?) : AbstractAssert<FindingAssert, Finding>(actual, FindingAssert::class.java) {

    fun hasMessage(expectedMessage: String) = apply {
        isNotNull()
        actual!!
        if (actual.message != expectedMessage) {
            throw failureWithActualExpected(
                actual.message,
                expectedMessage,
                """Expected message "$expectedMessage" but actual message was "${actual.message}"""",
            )
        }
    }

    fun noSuppress() = apply {
        isNotNull()
        actual!!
        if (actual.suppressReasons.isNotEmpty()) {
            throw failureWithActualExpected(
                actual.suppressReasons,
                emptyList<String>(),
                "Expect no suppressions but ${actual.suppressReasons} was found",
            )
        }
    }

    fun hasStartSourceLocation(line: Int, column: Int) = hasStartSourceLocation(SourceLocation(line, column))

    fun hasStartSourceLocation(expected: SourceLocation) = apply {
        isNotNull()
        actual!!
        val actual = actual.location.source
        if (actual != expected) {
            val code = this.actual.entity.ktElement.containingFile.text
            assertSourceLocationInRange(code, expected)
            throw failureWithActualExpected(
                code.addPinAt(actual),
                code.addPinAt(expected),
                "Expected start source location to be $expected but was $actual"
            )
        }
    }

    fun hasEndSourceLocation(line: Int, column: Int) = hasEndSourceLocation(SourceLocation(line, column))

    fun hasEndSourceLocation(expected: SourceLocation) = apply {
        isNotNull()
        actual!!
        val actual = actual.location.endSource
        if (actual != expected) {
            val code = this.actual.entity.ktElement.containingFile.text
            assertSourceLocationInRange(code, expected)
            throw failureWithActualExpected(
                code.addPinAt(actual),
                code.addPinAt(expected),
                "Expected end source location to be $expected but was $actual"
            )
        }
    }

    fun hasTextLocation(expected: Pair<Int, Int>) = hasTextLocation(TextLocation(expected.first, expected.second))

    fun hasTextLocation(expected: TextLocation) = apply {
        isNotNull()
        actual!!
        val actual = actual.location.text
        if (actual != expected) {
            throw failureWithActualExpected(
                actual,
                expected,
                "Expected text location to be $expected but was $actual"
            )
        }
    }

    fun hasTextLocation(expected: String) = apply {
        isNotNull()
        actual!!
        val code = actual.entity.ktElement.containingFile.text

        val index = code.indexOf(expected)
        require(index >= 0) { """The snippet "$expected" doesn't exist in the code""" }
        require(code.indexOf(expected, index + 1) < 0) {
            """The snippet "$expected" appears multiple times in the code"""
        }
        hasTextLocation(TextLocation(index, index + expected.length))
    }
}

private fun String.addPinAt(sourceLocation: SourceLocation): String = lines().toMutableList().apply {
    val line = this[sourceLocation.line - 1]
    this[sourceLocation.line - 1] = line.replaceRange(sourceLocation.column - 1, sourceLocation.column - 1, "📍")
}.joinToString("\n")

@Suppress("NOTHING_TO_INLINE") // avoid noise in the Stacktrace
private inline fun assertSourceLocationInRange(code: String, sourceLocation: SourceLocation) {
    val lines = code.lines()
    require(sourceLocation.line - 1 < lines.count()) {
        "The line ${sourceLocation.line} doesn't exist in the file. The file has ${lines.count()} lines"
    }
    require(sourceLocation.column - 1 <= lines[sourceLocation.line - 1].count()) {
        "The column ${sourceLocation.column} doesn't exist in the line ${sourceLocation.line}. The line has ${lines[sourceLocation.line - 1].count() + 1} columns"
    }
}
