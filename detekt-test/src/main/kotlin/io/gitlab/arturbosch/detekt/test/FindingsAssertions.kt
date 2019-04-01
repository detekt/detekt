package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.SourceLocation
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.AbstractListAssert
import org.assertj.core.util.Objects.areEqual

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

    fun hasLocationStrings(vararg expected: String, trimIndent: Boolean = false) {
        isNotNull
        val locationStrings = actual.asSequence().map { it.locationAsString }.sorted()
        if (trimIndent) {
            areEqual(locationStrings.map { it.trimIndent() }.toList(), expected.map { it.trimIndent() }.sorted())
        } else {
            areEqual(locationStrings.toList(), expected.toList().sorted())
        }
    }

    fun hasSourceLocations(vararg expected: SourceLocation) {
        isNotNull

        val actualSources = actual.asSequence()
                .map { it.location.source }
                .sortedWith(compareBy({ it.line }, { it.column }))

        val expectedSources = expected.asSequence()
                .sortedWith(compareBy({ it.line }, { it.column }))

        areEqual(actualSources.toList(), expectedSources.toList())
    }
}

class FindingAssert(val actual: Finding?) : AbstractAssert<FindingAssert, Finding>(actual, FindingAssert::class.java)
