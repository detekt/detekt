package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity

/**
 * This rule reports if tabs are used in Kotlin files.
 * According to
 * [Google's Kotlin style guide](https://android.github.io/kotlin-guides/style.html#whitespace-characters)
 * the only whitespace chars that are allowed in a source file are the line terminator sequence
 * and the ASCII horizontal space character (0x20).
 *
 * @author Misa Torres
 */
class NoTabs(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Checks if tabs are used in Kotlin files.",
			Debt.FIVE_MINS)

	fun visit(ktFileContent: KtFileContent) {
		ktFileContent.content.forEachIndexed { index, line ->
			if (line.contains('\t')) {
				report(CodeSmell(issue, Entity.from(ktFileContent.file),
						"Line ${index + 1} uses the tab character."))
			}
		}
	}
}
