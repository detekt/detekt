package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity

/**
 * This rule reports lines that end with a whitespace.
 *
 * @author Misa Torres
 */
class TrailingWhitespace(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Checks which lines end with a whitespace.",
			Debt.FIVE_MINS)

	fun visit(ktFileContent: KtFileContent) {
		ktFileContent.content.forEachIndexed { index, line ->
			if (line.isNotEmpty()) {
				val lastChar = line.last()
				if (lastChar == ' ' || lastChar == '\t') {
					report(CodeSmell(issue, Entity.from(ktFileContent.file, line.length - 1),
							"Line ${index + 1} ends with a whitespace."))
				}
			}
		}
	}
}
