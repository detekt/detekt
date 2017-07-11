package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Context

inline fun <T> T.reportFindings(context: Context, rules: () -> List<SubRule<T>>) {
	val findings = rules.invoke()
			.map { it.findings }
			.flatten()

	context.report(findings)
}