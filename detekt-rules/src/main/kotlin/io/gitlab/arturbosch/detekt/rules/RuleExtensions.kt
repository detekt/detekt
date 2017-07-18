package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Context

inline fun <T> T.reportFindings(context: Context, rules: () -> List<SubRule<T>>) {
	rules.invoke()
			.filter { it.isActive() }
			.onEach { it.apply(this) }
			.flatMap { it.findings }
			.apply { context.report(this) }
}