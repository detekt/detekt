package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtFile

class NewLineAtEndOfFile(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Checks whether files end with a line separator.",
			Debt.FIVE_MINS)

	override fun visitKtFile(file: KtFile) {
		val text = file.text
		if (text.isNotEmpty() && text.lastOrNull() != '\n') {
			report(CodeSmell(issue, Entity.from(file, text.length - 1)))
		}
	}

}
