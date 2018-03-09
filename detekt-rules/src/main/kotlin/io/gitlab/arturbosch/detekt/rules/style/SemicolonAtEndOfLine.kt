package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtFile

/**
 * This rule reports if a line ends with a semicolon.
 *
 * <noncompliant>
 * println();
 * // this is a comment;
 * </noncompliant>
 *
 * <compliant>
 * println()
 * println(); println()
 * // this is a comment
 * println(); // how to get away this rule
 * </compliant>
 *
 * @author Misa Torres
 */
class SemicolonAtEndOfLine(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Checks if a line ends with a semicolon.",
			Debt.FIVE_MINS)

	override fun visitKtFile(file: KtFile) {
		val lines = file.text.splitToSequence("\n")
		lines.forEachIndexed { index, line ->
			if (line.isNotEmpty() && line.endsWith(';')) {
				report(CodeSmell(issue, Entity.from(file),
						"Line ${index + 1} ends with a semicolon."))
			}
		}
	}
}
