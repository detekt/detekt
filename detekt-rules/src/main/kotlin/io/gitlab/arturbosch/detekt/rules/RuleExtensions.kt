package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.rules.style.File

inline fun <T : File> T.reportFindings(context: Context, rules: List<SubRule<T>>) {
	rules.filter { it.visitCondition(this.file) }
			.onEach { it.apply(this) }
			.flatMap { it.findings }
			.apply { context.report(this) }
}
