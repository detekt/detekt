package dev.detekt.test.assertj

import dev.detekt.api.Finding
import dev.detekt.api.Location
import dev.detekt.api.SourceLocation
import dev.detekt.api.TextLocation
import org.assertj.core.annotation.CheckReturnValue
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.AbstractListAssert

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
            val file = this.actual.entity.ktElement.containingFile.text
            throw failureWithActualExpected(
                file.substring(actual.start, actual.end),
                file.substring(expected.start, expected.end),
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

private val Finding.location: Location
    get() = entity.location

private fun String.addPinAt(sourceLocation: SourceLocation): String = lines().toMutableList().apply {
    val line = this[sourceLocation.line - 1]
    this[sourceLocation.line - 1] = line.replaceRange(sourceLocation.column - 1, sourceLocation.column - 1, "📍")
}.joinToString("\n")

@Suppress("NOTHING_TO_INLINE") // avoid noise in the Stacktrace
private inline fun assertSourceLocationInRange(code: String, sourceLocation: SourceLocation) {
    val lines = code.lines()
    if (sourceLocation.line - 1 >= lines.count()) {
        throw IndexOutOfBoundsException(
            "The line ${sourceLocation.line} doesn't exist in the file. The file has ${lines.count()} lines"
        )
    }
    if (sourceLocation.column - 1 > lines[sourceLocation.line - 1].count()) {
        throw IndexOutOfBoundsException(
            "The column ${sourceLocation.column} doesn't exist in the line ${sourceLocation.line}. The line has ${lines[sourceLocation.line - 1].count() + 1} columns"
        )
    }
}
