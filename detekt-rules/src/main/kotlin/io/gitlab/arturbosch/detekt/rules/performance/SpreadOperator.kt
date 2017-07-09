package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity

class SpreadOperator(config: Config = Config.empty) : Rule() {
	override val issue: Issue = Issue("SpreadOperator", Severity.Performance, "")
}