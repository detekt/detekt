package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtFile

class RedundantModifierRule(config: Config = Config.empty) : Rule(config) {
	override val issue: Issue = Issue("RedundantModifierRule", Severity.Style, "TODO")

	override fun visitKtFile(file: KtFile) {
		super.visitKtFile(file)
	}
}
