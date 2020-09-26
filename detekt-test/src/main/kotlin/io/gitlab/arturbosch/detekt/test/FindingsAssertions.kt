package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.AbstractListAssert
import java.util.Objects

fun assertThat(findings: List<Finding>) = FindingsAssert(findings)

fun assertThat(finding: Finding) = FindingAssert(finding)

fun List<Finding>.assert() = FindingsAssert(this)

class FindingsAssert(actual: List<Finding>) :
        AbstractListAssert<FindingsAssert, List<Finding>,
                Finding, FindingAssert>(actual, FindingsAssert::class.java) {

    override fun newAbstractIterableAssert(iterable: MutableIterable<Finding>): FindingsAssert {
        throw UnsupportedOperationException("not implemented")
    }

    override fun toAssert(value: Finding?, description: String?): FindingAssert =
            FindingAssert(value).`as`(description)

    fun hasSourceLocations(vararg expected: SourceLocation) = apply {
        val actualSources = actual.asSequence()
                .map { it.location.source }
                .sortedWith(compareBy({ it.line }, { it.column }))

        val expectedSources = expected.asSequence()
                .sortedWith(compareBy({ it.line }, { it.column }))

        if (!Objects.deepEquals(actualSources.toList(), expectedSources.toList())) {
            failWithMessage(
                "Expected source locations to be ${expectedSources.toList()} but was ${actualSources.toList()}"
            )
        }
    }

    fun hasSourceLocation(line: Int, column: Int) = apply {
        hasSourceLocations(SourceLocation(line, column))
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
            }
        }
        val code = requireNotNull(finding?.entity?.ktElement?.run { containingKtFile.text }) {
            "Finding expected to provide a KtElement."
        }

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
        if (expectedMessage.isNotBlank() && actual.message.isBlank()) {
            failWithMessage("Expected message <$expectedMessage> but finding has no message")
        }

        if (!actual.message.trim().equals(expectedMessage.trim(), ignoreCase = true)) {
            failWithMessage("Expected message <$expectedMessage> but actual message was <${actual.message}>")
        }
    }
}
