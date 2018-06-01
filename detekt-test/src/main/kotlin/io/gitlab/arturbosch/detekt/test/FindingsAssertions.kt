package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.AbstractListAssert
import org.assertj.core.internal.Objects

fun assertThat(findings: List<Finding>) = FindingsAssert(findings)

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
		val locationStrings = actual.map { it.locationAsString }
		if (trimIndent) {
			areEqual(locationStrings.map { it.trimIndent() }, expected.map { it.trimIndent() })
		} else {
			areEqual(locationStrings, expected.toList())
		}
	}

	private fun areEqual(actualLocationStrings: List<String>, expectedLocationStrings: List<String>) {
		Objects.instance()
				.assertEqual(writableAssertionInfo, actualLocationStrings, expectedLocationStrings.toList())
	}
}

class FindingAssert(actual: Finding?) : AbstractAssert<FindingAssert, Finding>(actual, FindingAssert::class.java)
