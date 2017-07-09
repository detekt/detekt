package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity

class UnsafeCast(config: Config = Config.empty) : Rule(config) {
	override val issue: Issue = Issue("UnsafeCast",
			Severity.Defect,
			"Cast operator throws an exception if the cast is not possible")
}