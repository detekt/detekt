package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.SourceLocation
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.AbstractListAssert
import org.assertj.core.internal.Objects

fun assertThat(findings: List<Finding>) = FindingsAssert(findings)

fun assertThat(finding: Finding) = FindingAssert(finding)

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

		val locationStrings = actual.asSequence().map { it.locationAsString }
		if (trimIndent) {
			areEqual(locationStrings.map { it.trimIndent() }.toList(), expected.map { it.trimIndent() })
		} else {
			areEqual(locationStrings.toList(), expected.toList())
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

	private fun <T> areEqual(actual: List<T>, expected: List<T>) {
		Objects.instance()
				.assertEqual(writableAssertionInfo, actual, expected)
	}
}

class FindingAssert(val actual: Finding?) : AbstractAssert<FindingAssert, Finding>(actual, FindingAssert::class.java)
