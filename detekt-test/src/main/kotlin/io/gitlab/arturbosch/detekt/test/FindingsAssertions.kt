package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Finding
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.AbstractListAssert
import org.assertj.core.internal.Objects

fun assertThat(findings: List<Finding>) = FindingsAssert(findings)
class FindingsAssert(actual: List<Finding>) :
		AbstractListAssert<FindingsAssert, List<Finding>,
				Finding, FindingAssert>(actual, FindingsAssert::class.java) {

	override fun toAssert(value: Finding?, description: String?): FindingAssert =
			FindingAssert(value).`as`(description)

	fun hasLocationStrings(vararg expected: String) {
		isNotNull
		val actualLocationStrings = actual.map { it.locationAsString }
		areEqual(actualLocationStrings, expected)
	}

	private fun areEqual(actualLocationStrings: List<String>, expectedLocationStrings: Array<out String>) {
		Objects.instance()
				.assertEqual(writableAssertionInfo, actualLocationStrings, expectedLocationStrings.toList())
	}
}

class FindingAssert(actual: Finding?) : AbstractAssert<FindingAssert, Finding>(actual, FindingAssert::class.java)
