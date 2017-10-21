package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Finding

fun <T> SubRule<T>.verify(param: T, findings: (List<Finding>) -> Unit) {
	this.apply(param)
	findings.invoke(this.findings)
}
