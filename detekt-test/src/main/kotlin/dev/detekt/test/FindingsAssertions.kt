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
fun assertThat(finding: Finding) = FindingAssert(finding)

class FindingsAssert(actual: List<Finding>) :
    AbstractListAssert<FindingsAssert, List<Finding>, Finding, FindingAssert>(actual, FindingsAssert::class.java) {

    override fun newAbstractIterableAssert(iterable: MutableIterable<Finding>): FindingsAssert =
        throw UnsupportedOperationException("not implemented")

    override fun toAssert(value: Finding?, description: String?): FindingAssert =
        FindingAssert(value).`as`(description)

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
}

class FindingAssert(val actual: Finding?) : AbstractAssert<FindingAssert, Finding>(actual, FindingAssert::class.java) {

    fun hasMessage(expectedMessage: String) = apply {
        if (expectedMessage.isNotBlank() && actual?.message.isNullOrBlank()) {
            failWithMessage("Expected message <$expectedMessage> but finding has no message")
        }

        if (!actual?.message?.trim().equals(expectedMessage.trim(), ignoreCase = true)) {
            failWithMessage("Expected message <$expectedMessage> but actual message was <${actual?.message}>")
        }
    }

    fun noSuppress() = apply {
        if (actual == null) {
            failWithMessage("Expect no null")
        } else if (actual.suppressReasons.isNotEmpty()) {
            failWithMessage("Expect no suppressions but ${actual.suppressReasons} was found")
        }
    }

    fun hasStartSourceLocation(line: Int, column: Int) = apply {
        hasStartSourceLocation(SourceLocation(line, column))
    }

    fun hasStartSourceLocation(expected: SourceLocation) = apply {
        val actual = actual!!.location.source
        if (actual != expected) {
            throw failureWithActualExpected(
                actual,
                expected,
                "Expected start source location to be $expected but was $actual"
            )
        }
    }

    fun hasEndSourceLocation(line: Int, column: Int) = apply {
        hasEndSourceLocation(SourceLocation(line, column))
    }

    fun hasEndSourceLocation(expected: SourceLocation) = apply {
        val actual = actual!!.location.endSource
        if (actual != expected) {
            throw failureWithActualExpected(
                actual,
                expected,
                "Expected end source location to be $expected but was $actual"
            )
        }
    }

    fun hasTextLocation(expected: Pair<Int, Int>) = apply {
        hasTextLocation(TextLocation(expected.first, expected.second))
    }

    fun hasTextLocation(expected: TextLocation) = apply {
        val actual = actual!!.location.text
        if (actual != expected) {
            throw failureWithActualExpected(
                actual,
                expected,
                "Expected text location to be $expected but was $actual"
            )
        }
    }

    fun hasTextLocation(expected: String) = apply {
        val code = actual!!.entity.ktElement.containingKtFile.text

        val index = code.indexOf(expected)
        if (index < 0) {
            failWithMessage("The snippet \"$expected\" doesn't exist in the code")
        } else {
            if (code.indexOf(expected, index + 1) >= 0) {
                failWithMessage("The snippet \"$expected\" appears multiple times in the code")
            }
        }

        hasTextLocation(TextLocation(index, index + expected.length))
    }
}
